package com.example.almacercaapp.model

data class User(
    var username: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var password: String = "",
    var useEmail: Boolean = false
)