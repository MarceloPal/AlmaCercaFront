package com.example.almacercaapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almacercaapp.data.repository.ProductRepository
import com.example.almacercaapp.model.ProductCategory
import com.example.almacercaapp.model.ProductDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminViewModel(private val productRepository: ProductRepository) : ViewModel() {

    // --- STATE MANAGEMENT ---
    val products = MutableStateFlow<List<ProductDto>>(emptyList())
    val categories = MutableStateFlow<List<ProductCategory>>(emptyList())
    val uiStatus = MutableStateFlow<String?>(null)

    // State for the "Add/Edit Product" screen/dialog
    val productName = MutableStateFlow("")
    val productDescription = MutableStateFlow("")
    val productPrice = MutableStateFlow("")
    val productStock = MutableStateFlow("")
    val productImageUrl = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<ProductCategory?>(null)
    
    private val _editingProductId = MutableStateFlow<String?>(null)
    val editingProductId: StateFlow<String?> = _editingProductId.asStateFlow()

    init {
        loadAllProducts()
        categories.value = getMockCategories()
    }

    fun loadAllProducts() {
        viewModelScope.launch {
            productRepository.getAllProducts().onSuccess {
                Log.d("AdminViewModel", "Productos cargados con éxito: ${it.size} productos.")
                products.value = it
            }.onFailure {
                Log.e("AdminViewModel", "Error al cargar productos", it)
                uiStatus.value = "Error al cargar productos: ${it.message}"
            }
        }
    }

    fun onSaveProduct() {
        val productId = _editingProductId.value
        if (productId != null) {
            onUpdateProduct(productId)
        } else {
            onCreateProduct()
        }
    }

    private fun onCreateProduct() {
        val newProduct = createProductDtoFromState(null)
        viewModelScope.launch {
            productRepository.createProduct(newProduct).onSuccess {
                uiStatus.value = "¡Producto creado con éxito!"
                loadAllProducts()
                resetProductFields()
            }.onFailure {
                uiStatus.value = "Error al crear el producto: ${it.message}"
            }
        }
    }

    private fun onUpdateProduct(productId: String) {
        val updatedProduct = createProductDtoFromState(productId)
        viewModelScope.launch {
            // ▼▼▼ ¡AQUÍ ESTÁ LA CORRECCIÓN! ▼▼▼
            // La función solo necesita el objeto `updatedProduct`, que ya contiene el ID.
            productRepository.updateProduct(updatedProduct).onSuccess {
                uiStatus.value = "¡Producto actualizado con éxito!"
                loadAllProducts()
                resetProductFields()
            }.onFailure {
                uiStatus.value = "Error al actualizar: ${it.message}"
            }
        }
    }

    fun deleteProduct(product: ProductDto) {
        viewModelScope.launch {
            if (product.id == null) {
                uiStatus.value = "Error: El producto no tiene un ID válido."
                return@launch
            }
            productRepository.deleteProduct(product.id).onSuccess {
                uiStatus.value = "Producto '${product.name}' eliminado."
                products.update { currentList -> currentList.filterNot { it.id == product.id } }
            }.onFailure {
                uiStatus.value = "Error al eliminar: ${it.message}"
            }
        }
    }

    fun loadProductForEdit(productId: String) {
        val product = products.value.find { it.id == productId } ?: return
        _editingProductId.value = product.id
        productName.value = product.name
        productDescription.value = product.description
        productPrice.value = product.price.toString()
        productStock.value = product.stock.toString()
        productImageUrl.value = product.imageUrl
        selectedCategory.value = categories.value.find { it.id == product.categoryId }
    }

    fun resetProductFields() {
        _editingProductId.value = null
        productName.value = ""
        productDescription.value = ""
        productPrice.value = ""
        productStock.value = ""
        productImageUrl.value = ""
        selectedCategory.value = null
    }

    private fun createProductDtoFromState(id: String?): ProductDto {
        return ProductDto(
            id = id,
            name = productName.value,
            description = productDescription.value,
            price = productPrice.value.toDoubleOrNull() ?: 0.0,
            stock = productStock.value.toIntOrNull() ?: 0,
            imageUrl = productImageUrl.value,
            storeId = "TIENDA_ADMIN_PRINCIPAL",
            categoryId = selectedCategory.value?.id ?: ""
        )
    }

    private fun getMockCategories(): List<ProductCategory> {
        return listOf(
            ProductCategory(id = "101", name = "Vegetales y Frutas", imageRes = 0, storeId = "2"),
            ProductCategory(id = "105", name = "Bebestibles", imageRes = 0, storeId = "1")
        )
    }
}
