package com.example.almacercaapp.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Este interceptor es el "guardia de seguridad" de nuestra red.
 * Ya no depende de UserRepository, sino directamente de DataStore para romper el ciclo.
 * Añade el header userId solo para rutas de carrito.
 */
class AuthInterceptor(private val dataStore: DataStore<Preferences>) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()

        // Lee el ID de usuario directamente desde DataStore.
        val userId = runBlocking {
            dataStore.data.map { preferences ->
                preferences[stringPreferencesKey("user_id")]
            }.first()
        }

        // Solo añade userId para rutas de carrito
        val needsUserId = url.contains("/api/cart")

        val newRequest = if (needsUserId && !userId.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("userId", userId)
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}