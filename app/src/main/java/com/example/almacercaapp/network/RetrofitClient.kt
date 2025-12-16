package com.example.almacercaapp.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * AHORA ES UNA CLASE. Esto nos permite inyectarle el DataStore que necesita
 * para crear el AuthInterceptor, de una forma limpia y sin ciclos.
 */
class RetrofitClient(dataStore: DataStore<Preferences>) {

    private val baseUrl = "http://192.168.1.83:8080/"

    val instance: ApiService

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Creamos el interceptor de autenticación, pasándole el DataStore.
        val authInterceptor = AuthInterceptor(dataStore)

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        instance = retrofit.create(ApiService::class.java)
    }
}
