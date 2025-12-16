package com.example.almacercaapp.model

// Define un ítem dentro del carrito (un producto + su cantidad)
data class CartItem(
    val product: Product,
    var quantity: Int
)

    