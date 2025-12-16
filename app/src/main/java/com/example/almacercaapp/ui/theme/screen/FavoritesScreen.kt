package com.example.almacercaapp.ui.theme.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.almacercaapp.R
import com.example.almacercaapp.model.Product
import com.example.almacercaapp.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    parentNavController: NavHostController,
    viewModel: FavoritesViewModel
) {
    // Observa los favoritos desde el ViewModel
    val favoriteProducts by viewModel.favoriteProducts.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Favoritos") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // Muestra un mensaje si la lista está vacía
            if (favoriteProducts.isEmpty()) {
                Text("Aún no tienes productos favoritos.")
            }
            // Muestra la lista de productos favoritos
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(favoriteProducts) { product ->
                        FavoriteItemRow(
                            product = product,
                            onClick = {
                                parentNavController.navigate("product_detail/${product.id}")
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

/**
 * Composable de ayuda para mostrar un producto favorito
 */
@Composable
private fun FavoriteItemRow(
    product: Product,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Usa Coil (AsyncImage) para cargar la imagen desde la URL
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(product.imageUrl)
                .crossfade(true)
                .error(R.drawable.placeholder_image) // Muestra esto si la imagen falla
                .build(),
            contentDescription = product.name,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = product.description.take(40) + "...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1
            )
        }

        Text(
            text = String.format("$%.0f", product.price),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "Ver detalle",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}