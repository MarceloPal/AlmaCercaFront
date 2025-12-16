package com.example.almacercaapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almacercaapp.data.repository.UserRepository
import com.example.almacercaapp.domain.validation.*
import com.example.almacercaapp.model.User
import com.example.almacercaapp.model.UserRole // <-- IMPORTACIÓN CORREGIDA
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    // --- Estados de la UI ---
    var user = mutableStateOf(User())
    var nameError = mutableStateOf<String?>(null)
    var emailError = mutableStateOf<String?>(null)
    var phoneError = mutableStateOf<String?>(null)
    var passwordError = mutableStateOf<String?>(null)
    var verificationError = mutableStateOf<String?>(null)
    var verificationCode = mutableStateOf("")
    var timeLeft = mutableStateOf(30)
    var canResend = mutableStateOf(false)
    var selectedRole = mutableStateOf(UserRole.BUYER)

    // --- Estados de Navegación y Sesión ---
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess = _registerSuccess.asStateFlow()

    // Este es el rol del usuario que ha iniciado sesión. Se actualiza automáticamente.
    val loggedInUserRole = mutableStateOf<UserRole?>(null)

    init {
        startCountdown()

        // --- ARQUITECTURA CORRECTA: Observar el rol del usuario --- 
        viewModelScope.launch {
            repository.userRoleFlow.collect { roleString ->
                // Convierte el String del DataStore al Enum UserRole
                loggedInUserRole.value = when (roleString?.uppercase()) { // Usamos uppercase para ser robustos
                    "ADMIN" -> UserRole.ADMIN
                    "BUYER" -> UserRole.BUYER
                    // El caso SELLER ha sido eliminado
                    else -> null // Si no hay rol guardado o es un valor inesperado, es null
                }
            }
        }
    }

    fun updateRole(newRole: UserRole) {
        selectedRole.value = newRole
    }

    fun startCountdown() {
        canResend.value = false
        timeLeft.value = 30
        viewModelScope.launch {
            while (timeLeft.value > 0) {
                delay(1000)
                timeLeft.value -= 1
            }
            canResend.value = true
        }
    }

    fun resendCode() {
        if (canResend.value) startCountdown()
    }

    fun submitLogin() {
        if (validateLoginFields()) {
            viewModelScope.launch {
                if (!user.value.useEmail) {
                    phoneError.value = "Inicio de sesión con teléfono no implementado."
                    return@launch
                }

                val result = repository.login(
                    email = user.value.email.trim(),
                    password = user.value.password
                )

                if (result.isSuccess) {
                    _loginSuccess.value = true
                    passwordError.value = null
                    emailError.value = null
                } else {
                    passwordError.value = result.exceptionOrNull()?.message ?: "Error al iniciar sesión"
                }
            }
        }
    }

   fun submitSignUp() {
        if (validateSignUpFields()) {
            viewModelScope.launch {
                val emailToRegister = if (user.value.useEmail) user.value.email.trim() else "${user.value.phoneNumber.trim()}@phone.local"
                val phoneToRegister = if (!user.value.useEmail) user.value.phoneNumber.trim() else ""

                val result = repository.register(
                    name = user.value.username.trim(),
                    email = emailToRegister,
                    phone = phoneToRegister,
                    password = user.value.password,
                    role = selectedRole.value
                )

                if (result.isSuccess) {
                    _registerSuccess.value = true
                    listOf(nameError, emailError, phoneError, passwordError).forEach { it.value = null }
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Error en el registro"
                    if (user.value.useEmail) emailError.value = errorMessage else phoneError.value = errorMessage
                }
            }
        }
    }

    // --- Navegación y Validación ---
    fun onLoginNavigationDone() {
        _loginSuccess.value = false
    }

    fun onRegisterNavigationDone() {
        _registerSuccess.value = false
    }

    private fun validateLoginFields(): Boolean {
        emailError.value = if (user.value.useEmail) validateEmail(user.value.email) else null
        passwordError.value = if (user.value.password.isNotBlank()) null else "La contraseña es obligatoria"
        val isIdentifierValid = if (user.value.useEmail) emailError.value == null && user.value.email.isNotBlank() else true
        return isIdentifierValid && passwordError.value == null
    }

    private fun validateSignUpFields(): Boolean {
        nameError.value = validateNameLettersOnly(user.value.username)
        emailError.value = if (user.value.useEmail) validateEmail(user.value.email) else null
        phoneError.value = if (!user.value.useEmail) validatePhoneDigitsOnly(user.value.phoneNumber) else null
        passwordError.value = validateStrongPass(user.value.password)
        return listOf(nameError.value, emailError.value, phoneError.value, passwordError.value).all { it == null }
    }
    
    fun validateVerification(): Boolean {
        verificationError.value = validateVerificationCode(verificationCode.value)
        return verificationError.value == null
    }

    // --- Actualizadores de Campos ---
    fun toggleMethod() { user.value = user.value.copy(useEmail = !user.value.useEmail) }
    fun updateUsername(value: String) { user.value = user.value.copy(username = value) }
    fun updateEmail(value: String) { user.value = user.value.copy(email = value) }
    fun updatePhone(value: String) { user.value = user.value.copy(phoneNumber = value) }
    fun updatePassword(value: String) { user.value = user.value.copy(password = value) }
    fun updateVerificationCode(value: String) { verificationCode.value = value }
}