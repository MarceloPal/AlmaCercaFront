package com.example.almacercaapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object para un Producto.
 * Esta clase debe ser un reflejo EXACTO de la estructura JSON que envía el backend.
 */
@Serializable
data class ProductDto(
    // Usamos @SerialName para mapear el "_id" de MongoDB al campo "id" de Kotlin.
    @SerialName("_id")
    val id: String? = null, // El ID que viene de la base de datos.

    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val storeId: String, // El ID de la tienda a la que pertenece.
    val categoryId: String, // El ID de la categoría a la que pertenece.
    val imageUrl: String // La URL de la imagen del producto.
)
