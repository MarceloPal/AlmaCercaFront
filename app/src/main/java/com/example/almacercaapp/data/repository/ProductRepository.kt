package com.example.almacercaapp.data.repository

import com.example.almacercaapp.model.Product
import com.example.almacercaapp.model.ProductDto
import com.example.almacercaapp.network.ApiService

/**
 * Repositorio para gestionar todos los datos relacionados con productos.
 * Actúa como la única fuente de verdad para los datos de productos, ya sea
 * que vengan de la red o de una futura caché local.
 */
class ProductRepository(private val apiService: ApiService) {

    // --- ¡SOLUCIÓN! Caché en memoria para los productos --- 
    private val productCache = mutableMapOf<String, ProductDto>()

    /**
     * Obtiene un producto específico por su ID.
     * PRIMERO busca en el caché. Si no lo encuentra, intenta la llamada a la red.
     */
    suspend fun getProductById(productId: String): Result<ProductDto> {
        // 1. Intentar obtener desde el caché
        val cachedProduct = productCache[productId]
        if (cachedProduct != null) {
            return Result.success(cachedProduct)
        }

        // 2. Si no está en caché, intentar la llamada a la red (que puede fallar por seguridad)
        return try {
            val allProductsResult = getAllProducts()
            allProductsResult.fold(
                onSuccess = {
                    val product = it.find { productDto -> productDto.id == productId }
                    if (product != null) {
                        Result.success(product)
                    } else {
                        Result.failure(Exception("Producto no encontrado con ID: $productId"))
                    }
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Llama al backend para obtener solo los productos de una categoría específica.
     * Y guarda los resultados en el caché para uso futuro.
     */
    suspend fun getProductsByCategory(categoryId: String): Result<List<ProductDto>> {
        return try {
            val response = apiService.getProductsByCategory(categoryId)
            if (response.isSuccessful && response.body() != null) {
                val products = response.body()!!
                // ¡Guardar en caché!
                products.forEach { product -> product.id?.let { productCache[it] = product } }
                Result.success(products)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Respuesta de error no disponible"
                Result.failure(Exception("Error ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Llama al backend para obtener TODOS los productos.
     * Y guarda los resultados en el caché.
     */
    suspend fun getAllProducts(): Result<List<ProductDto>> {
        return try {
            val response = apiService.getAllProductsFromDefaultStore()
            if (response.isSuccessful && response.body() != null) {
                val products = response.body()!!
                // ¡Guardar en caché!
                products.forEach { product -> product.id?.let { productCache[it] = product } }
                Result.success(products)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Respuesta de error no disponible"
                Result.failure(Exception("Error ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Funciones de Admin (crear, actualizar, eliminar) ---

    suspend fun createProduct(product: ProductDto): Result<ProductDto> {
        return try {
            val response = apiService.createProduct(product)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                 val errorBody = response.errorBody()?.string() ?: "Respuesta de error no disponible"
                Result.failure(Exception("Error ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
     suspend fun updateProduct(product: ProductDto): Result<ProductDto> {
        return try {
            val response = apiService.updateProduct(product.id!!, product)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Respuesta de error no disponible"
                Result.failure(Exception("Error ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            val response = apiService.deleteProduct(productId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Respuesta de error no disponible"
                Result.failure(Exception("Error ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Función de extensión para mapear un DTO a un modelo de UI.
 */
fun ProductDto.toProduct(): Product {
    return Product(
        id = this.id ?: "", // Maneja el id nulo con un valor por defecto
        name = this.name,
        description = this.description,
        price = this.price,
        categoryId = this.categoryId,
        storeId = this.storeId,
        stock = this.stock,
        imageUrl = this.imageUrl
    )
}
