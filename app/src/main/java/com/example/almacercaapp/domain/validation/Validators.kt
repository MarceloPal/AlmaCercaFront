package com.example.almacercaapp.domain.validation

import android.util.Patterns

// Validación del nombre: no vacío, solo letras y espacios
fun validateNameLettersOnly(nombre: String): String? {
    if (nombre.isBlank()) return "El nombre es obligatorio"
    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")
    return if (!regex.matches(nombre)) "Solo se aceptan letras y espacios" else null
}

// Validación del correo: formato y no vacío
fun validateEmail(email: String): String? {
    if (email.isBlank()) return "El correo es obligatorio"
    val ok = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return if (!ok) "Formato de correo inválido" else null
}

// Validación del teléfono: no vacío, longitud, solo números
fun validatePhoneDigitsOnly(phone: String): String? {
    if (phone.isBlank()) return "El teléfono es obligatorio"
    if (!phone.all { it.isDigit() }) return "Solo se aceptan números"
    if (phone.length !in 8..9) return "Debe contener entre 8 y 9 dígitos"
    return null
}

// Validación de contraseña segura
fun validateStrongPass(pass: String): String? {
    if (pass.isBlank()) return "Debes escribir tu contraseña"
    if (pass.length < 8) return "Debe tener una longitud de más de 7 caracteres"
    if (!pass.any { it.isUpperCase() }) return "Debe contener al menos una mayúscula"
    if (!pass.any { it.isDigit() }) return "Debe contener al menos un número"
    if (!pass.any { it.isLowerCase() }) return "Debe contener al menos una minúscula"
    if (!pass.any { !it.isLetterOrDigit() }) return "Debe contener al menos un caracter especial"
    if (pass.contains(' ')) return "No puede contener espacios en blanco"
    return null
}

// Validar que las contraseñas coincidan
fun validateConfirm(pass: String, confirm: String): String? {
    if (confirm.isBlank()) return "Debe confirmar su contraseña"
    return if (pass != confirm) "Las contraseñas no son iguales" else null
}

// Validar código de verificación
fun validateVerificationCode(code: String): String? {
    if (code.isBlank()) return "Debes ingresar el código"
    if (code.length != 4) return "El código debe tener 4 dígitos"
    if (!code.all { it.isDigit() }) return "El código solo puede contener números"
    return null
}