package com.example.almacercaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almacercaapp.data.repository.ProductRepository
import com.example.almacercaapp.model.Product
import com.example.almacercaapp.model.ProductDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla de carga de productos.
 */
data class ProductUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    // Expone solo el producto para que la vista lo observe de forma más simple
    // TODO: Considerar si es mejor que la vista observe el uiState completo
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    fun getProductById(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // Inicia la carga
            try {
                val result = repository.getProductById(productId)
                result.fold(
                    onSuccess = {
                        val product = it.toProduct()
                        _product.value = product // Actualiza el producto
                        _uiState.update { state -> state.copy(isLoading = false, product = product) }
                    },
                    onFailure = {
                        _uiState.update { state -> state.copy(isLoading = false, error = it.message) }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            // TODO: Lógica para guardar o actualizar el producto
        }
    }
}

/**
 * Función de extensión para mapear un DTO a un modelo de UI.
 */
fun ProductDto.toProduct(): Product {
    return Product(
        id = this.id ?: "", // Maneja el id nulo con un valor por defecto
        name = this.name,
        description = this.description,
        price = this.price,
        categoryId = this.categoryId,
        storeId = this.storeId,
        stock = this.stock,
        imageUrl = this.imageUrl
    )
}
