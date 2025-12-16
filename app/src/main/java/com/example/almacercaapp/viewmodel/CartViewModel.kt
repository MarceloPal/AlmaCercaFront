package com.example.almacercaapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almacercaapp.model.CartRepository
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CartRepository
) : ViewModel() {

    val uiState = repository.uiState

    fun incrementQuantity(productId: String) {
        viewModelScope.launch {
            repository.incrementQuantity(productId)
        }
    }

    fun decrementQuantity(productId: String) {
        viewModelScope.launch {
            repository.decrementQuantity(productId)
        }
    }

    fun clearCart() {
        Log.e("CULPABLE", "¡Alguien llamó a clearCart!")
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun removeItem(productId: String) {
        viewModelScope.launch {
            repository.removeItem(productId)
        }
    }

    fun messageShown() {
        repository.messageShown()
    }

}