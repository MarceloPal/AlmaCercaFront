package com.example.almacercaapp.model

data class Category(
    val id: Int,
    val name: String,
    // Usaremos drawables locales para simplificar. No necesitas Coil/Glide aún.
    @androidx.annotation.DrawableRes val imageRes: Int
)
