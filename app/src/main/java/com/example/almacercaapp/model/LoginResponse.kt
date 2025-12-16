package com.example.almacercaapp.model

data class LoginResponse(
    val id: String,
    val name: String,
    val email: String,
    val password: String? = null,
    val role: String
)