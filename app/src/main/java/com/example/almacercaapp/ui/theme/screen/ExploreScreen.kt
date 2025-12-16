package com.example.almacercaapp.ui.theme.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.almacercaapp.viewmodel.ExploreViewModel
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import android.provider.Settings
import android.net.Uri        // Para la clase Uri (Uri.fromParts)
// ▼▼▼ NUEVOS IMPORTS ▼▼▼
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
// ▲▲▲ FIN NUEVOS IMPORTS ▲▲▲


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class) // Añade ExperimentalMaterial3Api
@Composable
fun ExploreScreen(
    modifier: Modifier = Modifier,
    viewModel: ExploreViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // --- Gestión de Permisos ---
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            viewModel.startLocationUpdates(context)
        }
    }

    // --- UI Principal ---
    Column(modifier = modifier.fillMaxSize()) {

        // --- 1. BARRA DE BÚSQUEDA ---
        OutlinedTextField(
            value = "", // Valor de maqueta
            onValueChange = {}, // Acción vacía
            placeholder = { Text("¿Qué estás buscando? (Dirección, producto o negocio)") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.extraLarge, // Más redondeado
            colors = OutlinedTextFieldDefaults.colors( // Fondo gris claro
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                disabledContainerColor = Color(0xFFF5F5F5),
                focusedBorderColor = Color.Transparent, // Sin borde al enfocar
                unfocusedBorderColor = Color.Transparent // Sin borde normal
            ),
            enabled = false // Deshabilitado como maqueta
        )

        // --- 2. FILTROS Y RESULTADOS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row { // Agrupa los filtros
                FilterChip(
                    onClick = { /* Acción vacía */ },
                    label = { Text("Filtro") },
                    selected = false,
                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, "Expandir") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    onClick = { /* Acción vacía */ },
                    label = { Text("Clasificar") },
                    selected = false,
                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, "Expandir") }
                )
            }
            Text("35 resultados", color = Color.Gray, fontSize = 14.sp)
        }

        // --- 3. ENCABEZADO "CERCA DE TU ZONA" ---
        Text(
            text = "Cerca de tu zona...",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        // --- 4. CONTENEDOR DEL MAPA ---
        Box(modifier = Modifier.weight(1f)) { // El mapa ocupa el resto del espacio
            when {
                // Estado 1: Permiso concedido, muestra el mapa
                locationPermissionState.status.isGranted -> {
                    MapContent(
                        userLocation = uiState.userLocation,
                        stores = uiState.stores,
                        cameraPosition = uiState.cameraPosition
                    )
                }
                // Estado 2: Razón para el permiso
                locationPermissionState.status.shouldShowRationale -> {
                    PermissionRationale(locationPermissionState)
                }
                // Estado 3: Permiso denegado
                else -> {
                    PermissionDenied(context)
                }
            }
            // Indicador de carga
            if (uiState.isLoadingLocation && locationPermissionState.status.isGranted) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    } // Fin Column Principal

    // Limpia el listener de ubicación...
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopLocationUpdates()
        }
    }
}

// --- Composables de Ayuda ---

@Composable
fun MapContent(
    userLocation: LatLng?,
    stores: List<com.example.almacercaapp.model.Store>,
    cameraPosition: LatLng
) {
    // ... (Código de CameraPositionState y LaunchedEffect igual)
    // Controla la posición de la cámara del mapa
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cameraPosition, 15f) // Zoom inicial
    }

    // Actualiza la cámara si la posición del ViewModel cambia (ej. al obtener ubicación)
    LaunchedEffect(cameraPosition) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(cameraPosition, 15f),
            durationMs = 1000
        )
    }


    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = userLocation != null),
        uiSettings = MapUiSettings(myLocationButtonEnabled = true)
    ) {
        // Marcador para la ubicación del usuario (opcional)
        userLocation?.let {
            // Marker(...)
        }

        // Marcadores para las tiendas
        stores.forEach { store ->
            // ▼▼▼ LÓGICA PARA CAMBIAR EL MARCADOR ▼▼▼
            val icon: BitmapDescriptor = if (store.name.contains("Patricio", ignoreCase = true)) {
                // Si es "Patricio & Mily", usa un marcador verde (puedes crear uno)
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            } else {
                // Para las demás, usa el marcador rojo por defecto
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            }
            // ▲▲▲ FIN LÓGICA MARCADOR ▲▲▲

            Marker(
                state = MarkerState(position = LatLng(store.lat, store.lng)),
                title = store.name,
                snippet = store.address,
                icon = icon // <-- USA EL ÍCONO PERSONALIZADO
                // Puedes añadir onClick = { ... } para manejar clics en marcadores
            )
        }
    }
}

// ... (PermissionRationale y PermissionDenied se quedan igual)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRationale(locationPermissionState: PermissionState) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Necesitamos permiso de ubicación para mostrar tiendas cercanas.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
            Text("Conceder Permiso")
        }
    }
}

@Composable
fun PermissionDenied(
    context: Context // <-- ¡CONTEXTO RECIBIDO AQUÍ!
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Permiso de ubicación denegado.")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Por favor, activa el permiso en los ajustes de la aplicación.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // Navega a la configuración de la aplicación
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        ) {
            Text("Abrir Ajustes")
        }
    }
}