package com.example.almacercaapp.model

import android.util.Log
import androidx.datastore.core.DataStore
import com.example.almacercaapp.data.PreferencesKeys
import androidx.datastore.preferences.core.Preferences
import com.example.almacercaapp.network.AddToCartRequest
import com.example.almacercaapp.network.ApiService
import com.example.almacercaapp.network.UpdateQuantityRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.util.Locale

//estado del repo, solo guarda datos
data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val productAddedMessage: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val discount: Double = 0.0
) {
    private val subtotal: Double get() = items.sumOf { it.product.price * it.quantity }
    val impuestos: Double get() = subtotal * 0.19
    val total: Double get() = (subtotal + impuestos) * (1-discount)

    val formattedSubtotal: String get() = formatPrice(subtotal)
    val formattedImpuestos: String get() = formatPrice(impuestos)
    val formattedTotal: String get() = formatPrice(total)
    val totalItems: Int get() = items.sumOf { it.quantity }

    private fun formatPrice(price: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("es").setRegion("CL").build())
        format.maximumFractionDigits = 0
        return format.format(price)
    }

    private val totalPrice: Double get() = items.sumOf { it.product.price * it.quantity }
    val formattedTotalPrice: String get() {
        val format = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("es").setRegion("CL").build())
        format.maximumFractionDigits = 0
        return format.format(totalPrice)
    }
}

class CartRepository(
    private val apiService: ApiService,
    private val dataStore: DataStore<Preferences>
) {
    private val _uiState = MutableStateFlow(CartUiState()) //guarda
    val uiState = _uiState.asStateFlow() //lectura

    private suspend fun getUserId(): String? {
        return dataStore.data.map { prefs ->
            prefs[PreferencesKeys.USER_ID] // ✅ Usa la misma instancia
        }.first()
    } //obtiene la identidad del usuer logeado, para op sensibles q requieran header

    // Cargar carrito desde el backend
    suspend fun loadCart() {
        val userId = getUserId()
        if (userId.isNullOrBlank()) {
            Log.e("CartRepository", "No userId found")
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        try {
            val response = apiService.getMyCart(userId)
            if (response.isSuccessful) {
                val cartItems = response.body() ?: emptyList()
                // Convertir CartItemDto a CartItem con Product
                // Necesitarás hacer GET de cada producto o tener los productos ya cargados
                Log.d("CartRepository", "Cart loaded: ${cartItems.size} items")
                // Por ahora, solo actualizamos el estado
                _uiState.update { it.copy(isLoading = false) }
            } else {
                Log.e("CartRepository", "Error loading cart: ${response.code()}")
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar carrito") }
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception loading cart", e)
            _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
        }
    }

    // Agregar producto (sincroniza con backend)
    suspend fun addProduct(product: Product) {
        Log.d("CartRepository", "=== addProduct LLAMADO ===")
        Log.d("CartRepository", "productId: ${product.id}, nombre: ${product.name}")


        val userId = getUserId()
        Log.d("CartRepository", "userId recuperado del DataStore: $userId")

        if (userId.isNullOrBlank()) {
            Log.e("CartRepository", "No userId found, cannot add to cart")
            _uiState.update { it.copy(errorMessage = "Usuario no autenticado") }
            return
        }

        Log.d("CartRepository", "Adding product ${product.id} for user $userId")

        try {
            val request = AddToCartRequest(productId = product.id, quantity = 1)
            val response = apiService.addProductToMyCart(userId, request)

            if (response.isSuccessful) {
                Log.d("CartRepository", "Product added to backend successfully")

                // Actualizar estado local
                _uiState.update { currentState ->
                    val existingItem = currentState.items.find { it.product.id == product.id }
                    val newItems = if (existingItem != null) {
                        currentState.items.map {
                            if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                        }
                    } else {
                        currentState.items + CartItem(product = product, quantity = 1)
                    }
                    currentState.copy(
                        items = newItems,
                        productAddedMessage = "Producto agregado exitosamente"
                    )
                }
            } else {
                Log.e("CartRepository", "Error adding product: ${response.code()} - ${response.errorBody()?.string()}")
                _uiState.update { it.copy(errorMessage = "Error al agregar producto") }
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception adding product", e)
            _uiState.update { it.copy(errorMessage = "Error de red: ${e.message}") }
        }
    }

    // Incrementar cantidad (sincroniza con backend)
    suspend fun incrementQuantity(productId: String) {
        val userId = getUserId() ?: return
        val item = _uiState.value.items.find { it.product.id == productId } ?: return
        val newQuantity = item.quantity + 1

        try {
            val request = UpdateQuantityRequest(quantity = newQuantity)
            val response = apiService.updateCartItemQuantity(userId, productId, request)

            if (response.isSuccessful) {
                _uiState.update { currentState ->
                    val newItems = currentState.items.map {
                        if (it.product.id == productId) it.copy(quantity = newQuantity) else it
                    }
                    currentState.copy(items = newItems)
                }
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Error incrementing quantity", e)
        }
    }

    // Decrementar cantidad (sincroniza con backend)
    suspend fun decrementQuantity(productId: String) {
        val userId = getUserId() ?: return
        val item = _uiState.value.items.find { it.product.id == productId } ?: return

        if (item.quantity > 1) {
            val newQuantity = item.quantity - 1
            try {
                val request = UpdateQuantityRequest(quantity = newQuantity)
                val response = apiService.updateCartItemQuantity(userId, productId, request)

                if (response.isSuccessful) {
                    _uiState.update { currentState ->
                        val newItems = currentState.items.map {
                            if (it.product.id == productId) it.copy(quantity = newQuantity) else it
                        }
                        currentState.copy(items = newItems)
                    }
                }
            } catch (e: Exception) {
                Log.e("CartRepository", "Error decrementing quantity", e)
            }
        } else {
            removeItem(productId)
        }
    }

    // Eliminar item (sincroniza con backend)
    suspend fun removeItem(productId: String) {
        val userId = getUserId() ?: return

        try {
            val response = apiService.removeCartItem(userId, productId)

            if (response.isSuccessful) {
                _uiState.update { currentState ->
                    val newItems = currentState.items.filterNot { it.product.id == productId }
                    currentState.copy(items = newItems)
                }
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Error removing item", e)
        }
    }

    fun messageShown() {
        _uiState.update { it.copy(productAddedMessage = null, errorMessage = null) }
    }

    suspend fun clearCart() {
        val userId = getUserId() ?: return
        _uiState.value.items.forEach { item ->
            try {
                apiService.removeCartItem(userId, item.product.id)
            } catch (e: Exception) {
                Log.e("CartRepository", "Error clearing cart", e)
            }
        }
        _uiState.update { it.copy(items = emptyList()) }
    }

    // Función para comprar (Llama al backend y luego limpia la lista local)
    suspend fun buyCart(): Result<Unit> {
        val userId = getUserId() // Recuperamos el ID guardado
        if (userId.isNullOrBlank()) {
            return Result.failure(Exception("Usuario no encontrado"))
        }

        return try {
            // 1. Llamamos al endpoint nuevo que cambia el estado a PURCHASED
            val response = apiService.buyCart(userId)

            if (response.isSuccessful) {
                // 2. Si el backend dice OK, limpiamos la lista VISUALMENTE (Local)
                clearLocalCart()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error en la compra"))
            }
        } catch (e: Exception) {
            Log.e("ERROR_COMPRA", "La compra falló", e)
            Result.failure(e)
        }
    }

    // Función auxiliar para limpiar solo la UI sin borrar de la BD
    private fun clearLocalCart() {
        _uiState.update {
            it.copy(items = emptyList(),
                productAddedMessage = null,
                discount = 0.0,
                errorMessage = null)
        }
    }


    fun applyCoupon(code: String): Boolean {
        return if (code.uppercase() == "ALMA10") {
            _uiState.update {
                it.copy(
                    discount = 0.10,
                    productAddedMessage = "¡Cupón aplicado con éxito!"
                )
            }
            true // Devuelve VERDADERO si entró aquí
        } else {
            false // Devuelve FALSO si entró aquí por que es boolean
        }
    }

}