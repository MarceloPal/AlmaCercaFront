package com.example.almacercaapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.almacercaapp.model.FavoritesRepository
import com.example.almacercaapp.model.Product

class FavoritesViewModel : ViewModel() {

    val favoriteProducts = FavoritesRepository.favoriteProducts

    fun toggleFavorite(product: Product) {
        FavoritesRepository.toggleFavorite(product)
    }

    fun isFavorite(productId: String): Boolean {
        return FavoritesRepository.isFavorite(productId)
    }
}