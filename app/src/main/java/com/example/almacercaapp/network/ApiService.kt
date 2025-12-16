package com.example.almacercaapp.network

import com.example.almacercaapp.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Públicos
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    @GET("api/products")
    suspend fun getAllProductsFromDefaultStore(): Response<List<ProductDto>>

    @GET("api/products/category/{categoryId}")
    suspend fun getProductsByCategory(@Path("categoryId") categoryId: String): Response<List<ProductDto>>

    @GET("api/products/{id}")
    suspend fun getProductDetail(@Path("id") id: String): Response<ProductDto>

    // Carrito (requiere header userId)
    @GET("api/cart")
    suspend fun getMyCart(@Header("userId") userId: String): Response<List<CartItemDto>>

    @POST("api/cart/buy")
    suspend fun buyCart(@Header("userId") userId: String): Response<Void> // <--- Void con V mayúscula

    @POST("api/cart/add")
    suspend fun addProductToMyCart(
        @Header("userId") userId: String,
        @Body body: AddToCartRequest
    ): Response<CartItem>

    @GET("api/cart/items/{productId}")
    suspend fun getCartItem(
        @Header("userId") userId: String,
        @Path("productId") productId: String
    ): Response<CartItemDto>

    @PUT("api/cart/items/{productId}")
    suspend fun updateCartItemQuantity(
        @Header("userId") userId: String,
        @Path("productId") productId: String,
        @Body body: UpdateQuantityRequest
    ): Response<CartItem>

    @DELETE("api/cart/items/{productId}")
    suspend fun removeCartItem(
        @Header("userId") userId: String,
        @Path("productId") productId: String
    ): Response<Void>

    // Admin
    @POST("api/admin/products")
    suspend fun createProduct(@Body product: ProductDto): Response<ProductDto>

    @PUT("api/admin/products/{productId}")
    suspend fun updateProduct(
        @Path("productId") productId: String,
        @Body product: ProductDto
    ): Response<ProductDto>

    @DELETE("api/admin/products/{productId}")
    suspend fun deleteProduct(@Path("productId") productId: String): Response<Void>
}

// DTOs específicos para carrito (request bodies)
data class AddToCartRequest(
    val productId: String,
    val quantity: Int
)

data class UpdateQuantityRequest(
    val quantity: Int
)