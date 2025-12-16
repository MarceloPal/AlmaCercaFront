package com.example.almacercaapp.ui.theme.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.almacercaapp.ui.theme.component.ProductCard
import com.example.almacercaapp.viewmodel.CategoryProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    storeId: String?,
    categoryId: String?,
    viewModel: CategoryProductsViewModel,
    onBack: () -> Unit,
    parentNavController: NavController
) {
    // Observamos el estado completo de la UI desde el ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Efecto que se lanza UNA SOLA VEZ cuando la pantalla se compone por primera vez,
    // o si la categoryId cambia.
    LaunchedEffect(categoryId) {
        if (categoryId != null) {
            viewModel.loadProductsForCategory(categoryId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.categoryName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. Muestra el indicador de carga si está en proceso
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            // 2. Muestra un error si ocurrió alguno
            else if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            }
            // 3. Muestra un mensaje si la lista está vacía
            else if (uiState.products.isEmpty()) {
                Text("No hay productos en esta categoría.", modifier = Modifier.align(Alignment.Center))
            }
            // 4. Si todo está bien, muestra la lista de productos de la nube
            else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.products) { product ->
                        ProductCard(product = product, onProductClick = {
                            parentNavController.navigate("product_detail/${product.id}")
                        })
                    }
                }
            }
        }
    }
}
