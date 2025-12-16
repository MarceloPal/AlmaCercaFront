package com.example.almacercaapp.model
data class CartItemDto(
    val id: Long,
    // ⚠️ Importante: Usamos ProductDto aquí dentro, no Product
    val product: ProductDto,
    val quantity: Int
)