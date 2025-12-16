package com.example.almacercaapp.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Singleton que maneja favoritos solo en memoria (sin backend).
 */
object FavoritesRepository {

    private val _favoriteProducts = MutableStateFlow<List<Product>>(emptyList())
    val favoriteProducts = _favoriteProducts.asStateFlow()

    fun toggleFavorite(product: Product) {
        _favoriteProducts.update { currentList ->
            val isAlreadyFavorite = currentList.any { it.id == product.id }
            if (isAlreadyFavorite) {
                currentList.filterNot { it.id == product.id }
            } else {
                currentList + product
            }
        }
    }

    fun isFavorite(productId: String): Boolean {
        return _favoriteProducts.value.any { it.id == productId }
    }

    // Esta función ya no interactúa con el carrito; puedes eliminarla si no la usas
    // O dejarla comentada por si decides implementarla después
    /*
    fun addAllToCart() {
        // Implementar cuando tengas endpoints de carrito
    }
    */
}