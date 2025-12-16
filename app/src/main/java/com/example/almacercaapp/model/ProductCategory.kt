package com.example.almacercaapp.model

import androidx.annotation.DrawableRes

/**
 * Define una CATEGORÍA DE PRODUCTOS dentro de una tienda.
 * Ej: "Lácteos", "Bebestibles", "Carnes".
 * IDs ahora son String para ser consistentes con el backend.
 */
data class ProductCategory(
    val id: String, // ID único de esta categoría (ej: "101")
    val name: String, // ej: "Lácteos"
    @DrawableRes val imageRes: Int,
    val storeId: String // La FK que dice a qué tienda pertenece (ej: "TIENDA_ADMIN_PRINCIPAL")
)
