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
) //clase de estados, contiene lo necesario para dibujar la pagina

class CheckoutViewModel(
    private val cartRepository: CartRepository //injectamos el repositorio, para que funcione
) : ViewModel() {

    // 1. Conecta con el Repositorio para obtener los productos y totales
    val cartUiState = cartRepository.uiState

    // 2. Estado mutable para los campos de la UI
    //_uistare guarda el estado de la pantalla y contiene todo el checkoutuistate
    private val _uiState = MutableStateFlow(CheckoutUiState()) //guarda el estado, solo el viewmodel puede modificarla
    val uiState = _uiState.asStateFlow() // Expose immutable state, solo la vista puede leer
    //guarda el texto q el user escribio en la caja


    fun onPaymentDetailsChanged(text: String) {
        _uiState.update { it.copy(paymentDetails = text) } // Actualiza el estado con el nuevo texto
    }

    //guarda el texto q el user escribio en la caja
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
        //de _uiState obtenemos el value (el estado actual) y de ese estado obtenemos b el valor de promoCode
        val code = _uiState.value.promoCode

        // Llamamos al repositorio y guardamos el resultado (true o false)
        val success = cartRepository.applyCoupon(code)

        if (success) {
            //se actualiza el estado
            // Si funcionó: Limpiamos cualquier error visual
            _uiState.update { it.copy(promoError = null,
                                      promoSuccess = "Cupón aplicado con éxito") }
        } else {
            // Si falló: Mostramos el error rojo debajo del campo de texto
            _uiState.update { it.copy(promoError = "Cupón inválido",
                                      promoSuccess = null) }
        }
    }


    fun onConfirmPurchase() { //realizar compra
        viewModelScope.launch { //corrutina para no congelar al buscar buy
            // Llamamos a la función nueva del repositorio
            // para ejecutar el endpoint
            val result = cartRepository.buyCart()

            if (result.isSuccess) {
                _uiState.update { it.copy(showConfirmDialog = true, //se muestra el dialogo
                                          promoCode = "",
                                          promoSuccess = null) }
            } else {
                // Manejar error (mostrar un Toast o mensaje)

            }
        }
    }

    fun onDialogDismiss() {
        _uiState.update { it.copy(showConfirmDialog = false) } //se cierre
    }
}