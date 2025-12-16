package com.example.almacercaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.almacercaapp.data.repository.ProductRepository

/**
 * Factory para crear una instancia de CategoryProductsViewModel,
 * inyectándole el ProductRepository que necesita para funcionar.
 */
class CategoryProductsViewModelFactory(
    private val productRepository: ProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryProductsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryProductsViewModel(productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for CategoryProducts")
    }
}
