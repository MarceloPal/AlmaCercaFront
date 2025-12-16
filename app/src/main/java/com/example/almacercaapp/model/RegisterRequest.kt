package com.example.almacercaapp.model

// Petición que se envía al backend para registrar un nuevo usuario.
// AHORA CONTIENE TODOS LOS CAMPOS NECESARIOS.
data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String
)
