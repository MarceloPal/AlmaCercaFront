package com.example.almacercaapp.ui.theme.screen


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController // <-- Se necesita para la navegación
import com.example.almacercaapp.R
import com.example.almacercaapp.model.ProductCategory
import com.example.almacercaapp.ui.theme.component.SearchBarComponent
import com.example.almacercaapp.viewmodel.StoreDetailUiState
import com.example.almacercaapp.viewmodel.StoreDetailViewModel


/**
 * Composable "Inteligente": Gestiona el estado y la lógica de la pantalla.
 */
@Composable
fun StoreDetailScreen(
    storeId: String?,
    onBack: () -> Unit,
    parentNavController: NavHostController, // <-- 1. RECIBE el NavController principal
    viewModel: StoreDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Carga los detalles de la tienda una sola vez cuando la pantalla aparece
    LaunchedEffect(key1 = storeId) {
        viewModel.loadStoreDetails(storeId)
    }

    // Decide qué mostrar: Carga, Contenido o Error.
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (uiState.store != null) {
        // Pasa el estado completo y el NavController al Composable que dibuja la UI
        StoreDetailContent(
            uiState = uiState,
            onBack = onBack,
            parentNavController = parentNavController // <-- 2. PÁSALO hacia abajo
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tienda no encontrada")
        }
    }
}

/**
 * Composable "Tonto": Solo se encarga de DIBUJAR la UI
 * recibiendo los datos ya listos y el NavController para las acciones.
 */
@Composable
fun StoreDetailContent(
    uiState: StoreDetailUiState,
    onBack: () -> Unit,
    parentNavController: NavHostController
) {
    // ¡CLAVE! Obtenemos la tienda desde el uiState que recibimos.
    val store = uiState.store!!

    // Obtenemos el nombre de la categoría ("Botillería") a partir del ID (2) que tiene la tienda.
    // Lo leemos del uiState, que es la fuente de verdad que nos da el ViewModel.
    val storeCategoryName = uiState.storeCategoryName
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        // Espaciado para la cuadrícula de categorías
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp) // Padding solo en la parte inferior
    ) {
        // --- CABECERA Y CONTENIDO SUPERIOR ---
        // Ocupa todo el ancho de la pantalla
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    val headerImage = getHeaderImageForCategory(storeCategoryName)
                    Image(
                        painter = painterResource(id = headerImage),
                        contentDescription = "Imagen de ${store.name}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver atrás", tint = Color.White)
                        }
                        IconButton(onClick = { /* TODO */ }, modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Más opciones", tint = Color.White)
                        }
                    }
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 30.dp)
                    ) {
                        Image(
                            painter = painterResource(id = store.logoRes),
                            contentDescription = "Logo de ${store.name}",
                            modifier = Modifier.size(90.dp).padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Contenido debajo de la cabecera
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(text = store.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) { /* ... */ }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = store.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { /* ... */ }
                    Spacer(modifier = Modifier.height(16.dp))
                    SearchBarComponent(
                        query = "", onQueryChange = {}, onSearch = {},
                        searchResults = emptyList(), placeholderText = "Busca el artículo en la tienda"
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // --- CUADRÍCULA DE CATEGORÍAS DE PRODUCTOS ---
        // Se añaden los items a la grid, pero con un padding horizontal para que no peguen a los bordes
        items(uiState.productCategories, key = { it.id }) { category ->
            Box(modifier = Modifier.padding(horizontal = if (uiState.productCategories.indexOf(category) % 2 == 0) 16.dp else 0.dp, // Padding izquierdo para la primera columna
            )) {
                ProductCategoryItem(
                    productCategory = category,
                    onClick = {
                        parentNavController.navigate(
                            "products/${store.id}/${category.id}"
                        )
                    }
                )
            }
        }
    }
}



/**
 * Función ayudante que devuelve el ID de la imagen de cabecera
 * basado en la categoría de la tienda.
 */
@Composable
private fun getHeaderImageForCategory(category: String): Int {
    return when (category.lowercase()) {
        "almacen", "almacen & bazar" -> R.drawable.header_almacenamasanderia
        "bazar" -> R.drawable.header_bazar
        "minimarket" -> R.drawable.header_minimarket
        "botillería" -> R.drawable.header_botilleria
        else -> R.drawable.header_default // Asegúrate de tener esta imagen
    }
}

/**
 * Composable reutilizable para cada tarjeta de CATEGORÍA de producto en la cuadrícula.
 */
@Composable
fun ProductCategoryItem(productCategory: ProductCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5FDFF)),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = productCategory.imageRes),
                contentDescription = productCategory.name,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = productCategory.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
