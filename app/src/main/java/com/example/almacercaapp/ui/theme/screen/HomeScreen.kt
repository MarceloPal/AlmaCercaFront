package com.example.almacercaapp.ui.theme.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.almacercaapp.model.Category
import com.example.almacercaapp.model.Store
import com.example.almacercaapp.ui.theme.component.HeaderLogo
import com.example.almacercaapp.ui.theme.component.SearchBarComponent
import com.example.almacercaapp.viewmodel.HomeViewModel

// ----------------------------------------------------
// MODELOS UI
// ----------------------------------------------------

data class QuickAccessButton(
    val text: String,
    val imageVector: ImageVector
)

val quickAccessButtons = listOf(
    QuickAccessButton("Favoritos", Icons.Default.FavoriteBorder),
    QuickAccessButton("Historial", Icons.Default.History),
    QuickAccessButton("Pedidos", Icons.AutoMirrored.Filled.ReceiptLong)
)

// ----------------------------------------------------
// HEADER PRINCIPAL
// ----------------------------------------------------

@Composable
fun HomeHeader() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(), // ✅ Evita superposición con notificaciones
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderLogo(modifier = Modifier.size(40.dp))

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "AlmaCerca",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "¡Qué alegría verte!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

// ----------------------------------------------------
// BOTÓN ACCESO RÁPIDO
// ----------------------------------------------------

@Composable
fun QuickAccessButtonItem(buttonInfo: QuickAccessButton) {
    Surface(
        onClick = { },
        shape = MaterialTheme.shapes.small,
        color = Color.White,
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = buttonInfo.imageVector,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.DarkGray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = buttonInfo.text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
        }
    }
}

// ----------------------------------------------------
// HOME SCREEN
// ----------------------------------------------------

@Composable
fun HomeScreen(
    parentNavController: NavHostController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // ✅ HEADER
        HomeHeader()

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

            // BÚSQUEDA
            SearchBarComponent(
                query = uiState.query,
                onQueryChange = homeViewModel::onQueryChange,
                onSearch = homeViewModel::onSearch,
                searchResults = uiState.searchResults
            )

            Spacer(modifier = Modifier.height(16.dp))

            // BOTONES RÁPIDOS
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(quickAccessButtons) { button ->
                    QuickAccessButtonItem(button)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BANNER
            uiState.bannerRes?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Image(
                        painter = painterResource(it),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // CATEGORÍAS
            HomeSectionHeader("Comprar por Categoría")

            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(uiState.categories) { category ->
                    CategoryItem(category)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // TIENDAS
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tiendas Cerca de Ti",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Ver más",
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(uiState.storesInYourArea) { store ->
                    val categoryName =
                        uiState.categories.find { it.id == store.storeCategoryId }?.name ?: ""

                    StoreItem(
                        store = store,
                        categoryName = categoryName
                    ) {
                        parentNavController.navigate("details/${store.id}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ----------------------------------------------------
// COMPONENTES AUXILIARES
// ----------------------------------------------------

@Composable
fun HomeSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2E7D32),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun CategoryItem(category: Category) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
            modifier = Modifier.size(80.dp)
        ) {
            Image(
                painter = painterResource(category.imageRes),
                contentDescription = category.name,
                modifier = Modifier.padding(10.dp),
                contentScale = ContentScale.Fit
            )
        }
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun StoreItem(
    store: Store,
    categoryName: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Image(
                painter = painterResource(store.logoRes),
                contentDescription = store.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = store.name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
