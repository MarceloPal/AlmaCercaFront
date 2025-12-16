package com.example.almacercaapp.model

/**
 * Representa el modelo de datos de un Producto para la capa de UI.
 * Esta clase está alineada con los datos que vienen del servidor.
 */
data class Product(
    val id: String,
    val categoryId: String,
    val name: String,
    val description: String,
    val price: Double,
    val storeId: String,
    val stock: Int,
    val imageUrl: String // Usamos la URL de la imagen en lugar de un recurso local
)
