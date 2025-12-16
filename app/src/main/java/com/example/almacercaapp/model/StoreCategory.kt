package com.example.almacercaapp.model

import androidx.annotation.DrawableRes

data class StoreCategory(
    val id: Int, // ID único de esta categoría (ej: 101 para Lácteos)
    val name: String,
    @DrawableRes val imageRes: Int
)
