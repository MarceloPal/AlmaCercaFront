package com.example.almacercaapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.lifecycle.ViewModel
import com.example.almacercaapp.R
import com.example.almacercaapp.model.Store // Reutiliza tu modelo Store
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ExploreUiState(
    val userLocation: LatLng? = null, // Ubicación actual del usuario
    val stores: List<Store> = emptyList(),
    val isLoadingLocation: Boolean = true,
    val cameraPosition: LatLng = LatLng(-33.4489, -70.6693) // Posición inicial (Santiago centro)
)

class ExploreViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // --- SIMULACIÓN DE TIENDAS (Usa tu lista real si la tienes centralizada) ---
    private val allStores = listOf(
        // Añade LatLng a tu modelo Store o crea un wrapper
        Store(id = 1, name = "Botillería La Esquina", storeCategoryId = 502, address = "Av. Siempre Viva 123", distance = "150 m", logoRes = R.drawable.logo_esquina, rating = 4.8f, description = "...", lat = -33.3134, lng = -70.5835),
        Store(id = 2, name = "Almacen y Bazar Sandra", storeCategoryId = 505, address = "Calle Falsa 456", distance = "300 m", logoRes = R.drawable.logo_sandra, rating = 4.5f, description = "...", lat = -33.3150, lng = -70.5840),
        Store(id = 3, name = "Almacen Mar", storeCategoryId = 505, address = "Pasaje Inventado 789", distance = "500 m", logoRes = R.drawable.logo_mariluna, rating = 4.2f, description = "...", lat = -33.3142, lng = -70.5860)
        // Añade más tiendas con sus coordenadas LatLng
    )

    init {
        // Carga inicial de tiendas (en una app real vendría de DB/API)
        _uiState.update { it.copy(stores = allStores) }
    }

    // --- Lógica de Ubicación ---

    @SuppressLint("MissingPermission") // Los permisos se verifican en la UI
    fun startLocationUpdates(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L) // Actualiza cada 10 seg
            .setMinUpdateIntervalMillis(5000L) // Mínimo 5 seg entre actualizaciones
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val newLatLng = LatLng(location.latitude, location.longitude)
                    _uiState.update {
                        it.copy(
                            userLocation = newLatLng,
                            isLoadingLocation = false,
                            // Mueve la cámara a la ubicación del usuario la primera vez
                            cameraPosition = if (it.userLocation == null) newLatLng else it.cameraPosition
                        )
                    }
                    // Opcional: Detener actualizaciones después de la primera ubicación
                    // stopLocationUpdates()
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun onCleared() {
        stopLocationUpdates()
        super.onCleared()
    }
}

