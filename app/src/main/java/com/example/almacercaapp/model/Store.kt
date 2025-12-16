package com.example.almacercaapp.model

import androidx.annotation.DrawableRes

data class Store(
    val id: Int,
    val name: String,
    val storeCategoryId: Int,
    val address: String,
    val distance: String,
    // Esta propiedad guardará el ID del recurso de imagen (ej. R.drawable.logo_sandra)
    @DrawableRes val logoRes: Int,
    val rating: Float, // ej: 4.7f
    val description: String,
    val lat: Double = 0.0, // Latitud
    val lng: Double = 0.0  // Longitud
)
