package com.example.almacercaapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almacercaapp.data.repository.ProductRepository
import com.example.almacercaapp.model.CartRepository
import com.example.almacercaapp.model.FavoritesRepository
import com.example.almacercaapp.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class ProductDetailUiState(
    val product: Product? = null,
    val quantity: Int = 1,
    val totalPrice: Double = 0.0,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val formattedTotalPrice: String get() {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        format.maximumFractionDigits = 0
        return format.format(totalPrice)
    }
}

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadProduct(productId: String?) {
        if (productId == null) {
            _uiState.update { it.copy(isLoading = false, error = "ID de producto no válido.") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        Log.d("ProductDetailVM", "Cargando producto ID: $productId")

        viewModelScope.launch {
            productRepository.getProductById(productId)
                .onSuccess { productDto ->
                    Log.d("ProductDetailVM", "Producto cargado con éxito: ${productDto.name}")
                    val product = productDto.toProduct()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            product = product,
                            quantity = 1,
                            totalPrice = product.price,
                            isFavorite = FavoritesRepository.isFavorite(product.id)
                        )
                    }
                }
                .onFailure { error ->
                    Log.e("ProductDetailVM", "Error al cargar el producto con ID $productId", error)
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun onQuantityChanged(change: Int) {
        _uiState.update { currentState ->
            val product = currentState.product ?: return@update currentState
            val newQuantity = (currentState.quantity + change).coerceIn(1, 99)
            currentState.copy(
                quantity = newQuantity,
                totalPrice = product.price * newQuantity
            )
        }
    }

    fun onToggleFavorite() {
        val product = _uiState.value.product ?: return
        FavoritesRepository.toggleFavorite(product)
        _uiState.update { it.copy(isFavorite = !it.isFavorite) }
    }

    fun onAddToCart() {
        val product = _uiState.value.product
        val quantity = _uiState.value.quantity

        Log.d("ProductDetailVM", "=== onAddToCart INICIADO ===")
        Log.d("ProductDetailVM", "product: ${product?.name}")
        Log.d("ProductDetailVM", "quantity: $quantity")

        if (product == null) {
            Log.e("ProductDetailVM", "ERROR: product es null, abortando")
            return
        }

        Log.d("ProductDetailVM", "Lanzando coroutine para agregar al carrito")
        viewModelScope.launch {
            repeat(quantity) {
                Log.d("ProductDetailVM", "Agregando producto (intento ${it + 1}/$quantity)")
                cartRepository.addProduct(product)
            }
            Log.d("ProductDetailVM", "=== onAddToCart COMPLETADO ===")
        }
    }
}