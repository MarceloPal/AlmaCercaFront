package com.example.almacercaapp.ui.theme.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.almacercaapp.model.Store
import com.example.almacercaapp.ui.theme.component.SearchBarComponent
import com.example.almacercaapp.model.Category
import com.example.almacercaapp.viewmodel.HomeViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.navigation.NavHostController

//botones de acceso rapido
data class QuickAccessButton(
    val text: String,
    val imageVector: ImageVector
)

val quickAccessButtons = listOf(
    QuickAccessButton("Favoritos", Icons.Default.FavoriteBorder),
    QuickAccessButton("Historial", Icons.Default.History),
    QuickAccessButton("Pedidos", Icons.AutoMirrored.Filled.ReceiptLong)
)

//es composable porque su trabajo es dibujar algo en la pantalla
@Composable
fun QuickAccessButtonItem(buttonInfo: QuickAccessButton) {
    OutlinedButton(
        onClick = { /* TODO: Acción de navegación futura */ },
        shape = MaterialTheme.shapes.large, // Borde bien redondeado
        border = BorderStroke(1.dp, Color.LightGray) // Borde gris claro
    ) {
        Icon(
            imageVector = buttonInfo.imageVector,
            contentDescription = null, // El texto del botón ya lo describe
            modifier = Modifier.size(16.dp),
            tint = Color.DarkGray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = buttonInfo.text,
            color = Color.DarkGray,
            style = MaterialTheme.typography.labelMedium
        )
    }

}

@Composable
fun HomeScreen(
    parentNavController: NavHostController,// se utilizara al hacer click en la tienda
    homeViewModel: HomeViewModel = viewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()

    // Para que toda la pantalla sea deslizable verticalmente
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ){
        Spacer(modifier = Modifier.height(16.dp))

        // 1. Barra de Búsqueda
        SearchBarComponent(
            query = uiState.query,
            onQueryChange = homeViewModel::onQueryChange,
            onSearch = homeViewModel::onSearch,
            searchResults = uiState.searchResults
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickAccessButtons) { buttonInfo ->
                // Llama al nuevo Composable que acabas de pegar
                QuickAccessButtonItem(buttonInfo = buttonInfo)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 2. Banner Promocional
        uiState.bannerRes?.let { bannerRes ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = bannerRes),
                    contentDescription = "Promoción",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        // 3. Sección de Categorías
        Text(
            text = "Categorías",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color=Color(0xFF2E7D32) // Verde AlmaCerca
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.categories) { category ->
                CategoryItem(category = category)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        // 4. Sección "En tu zona"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Alinea el texto y el ícono verticalmente
        ) {
            Text(
                text = "En tu zona",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF2E7D32) // Verde AlmaCerca
            )
            Spacer(modifier = Modifier.weight(1f)) // Este 'Spacer' empuja la flecha hacia el extremo derecho
            Icon(
                // Usamos un ícono de flecha nativo de Material
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Ver más en tu zona",
                tint = Color(0xFF2E7D32) // Le damos el mismo color verde
            )
        }
        Spacer(modifier = Modifier.height(8.dp)) // El espaciador se mantiene, pero fuera del Row


        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.storesInYourArea) { store ->
                val categoryName = uiState.categories.find { it.id == store.storeCategoryId }?.name ?: ""
                StoreItem(
                    store = store,
                    categoryName = categoryName, // <-- Le pasamos el nombre
                    onClick ={
                        parentNavController.navigate("details/${store.id}")
                    })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
/**
 * Composable reutilizable para cada ítem de Categoría.
 * AQUÍ es donde solucionas el tamaño de las imágenes de categorías.
 */
@Composable
fun CategoryItem(category: Category) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { /* Futura acción al hacer clic */ }
    ) {
        Image(
            painter = painterResource(id = category.imageRes),
            contentDescription = category.name,
            // ▼▼▼ SOLUCIÓN DE TAMAÑO ▼▼▼
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Crop // o ContentScale.Fit, según prefieras
            // ▲▲▲ FIN DE LA SOLUCIÓN ▲▲▲
        )
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}


/**
 * Composable reutilizable para cada ítem de Tienda.
 * AQUÍ es donde solucionas el tamaño de los logos de las tiendas.
 */
@Composable
fun StoreItem(
    store: Store,
    categoryName: String, // <-- Acepta el nombre de la categoría como parámetro
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick)  //{ /* Futura navegación a detalles de la tienda */ }
    ) {
        Card(shape = MaterialTheme.shapes.medium) {
            Image(
                painter = painterResource(id = store.logoRes), // Asumiendo que tu Store tiene logoRes
                contentDescription = store.name,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = store.name,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = categoryName,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }

}