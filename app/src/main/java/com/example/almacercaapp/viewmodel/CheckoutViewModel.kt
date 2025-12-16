package com.example.almacercaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almacercaapp.model.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI específico para esta pantalla
data class CheckoutUiState(
    val paymentDetails: String = "Visa *1234", // Valor inicial de maqueta
    val promoCode: String = "",
    val promoError: String? = null,
    val showConfirmDialog: Boolean = false, //
    val promoSuccess: String? = null
)

class CheckoutViewModel(
    private val cartRepository: CartRepository //injectamos el repositorio
) : ViewModel() {

    // 1. Conecta con el Repositorio para obtener los productos y totales
    val cartUiState = cartRepository.uiState

    // 2. Estado mutable para los campos de la UI
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow() // Expose immutable state


    fun onPaymentDetailsChanged(text: String) {
        _uiState.update { it.copy(paymentDetails = text) }
    }

    fun onPromoCodeChanged(text: String) {
        _uiState.update {
            it.copy(
                promoCode = text,
                promoError = null,
                promoSuccess = null
            )
        } // Limpia el error al escribir
    }

    fun verifyPromoCode() {
        val code = _uiState.value.promoCode

        // Llamamos al repositorio y guardamos el resultado (true o false)
        val success = cartRepository.applyCoupon(code)

        if (success) {
            // Si funcionó: Limpiamos cualquier error visual
            _uiState.update { it.copy(promoError = null,
                promoSuccess = "Cupón aplicado con éxito") }
        } else {
            // Si falló: Mostramos el error rojo debajo del campo de texto
            _uiState.update { it.copy(promoError = "Cupón inválido", promoSuccess = null) }
        }
    }


    fun onConfirmPurchase() {
        viewModelScope.launch {
            // Llamamos a la función nueva del repositorio
            // No necesitamos pasar userId ni apiService, el repo ya los tiene.
            val result = cartRepository.buyCart()

            if (result.isSuccess) {
                _uiState.update { it.copy(showConfirmDialog = true,
                                          promoCode = "",
                                           promoSuccess = null) }
            } else {
                // Manejar error (mostrar un Toast o mensaje)

            }
        }
    }

    fun onDialogDismiss() {
        _uiState.update { it.copy(showConfirmDialog = false) }
    }
}