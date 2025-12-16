package com.example.almacercaapp.ui.theme.screen
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.* // Importa todo lo de Material3 (incluye SnackbarHostState)
import androidx.compose.runtime.* // Importa remember, LaunchedEffect, etc.
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.almacercaapp.model.CartItem
import com.example.almacercaapp.viewmodel.CartViewModel
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    cartViewModel: CartViewModel = viewModel(),
    parentNavController: NavHostController
) {
    val uiState by cartViewModel.uiState.collectAsState()

    // 1. CREAMOS EL ESTADO DEL SNACKBAR (El mensajero)
    val snackbarHostState = remember { SnackbarHostState() }

    // 2. ESCUCHAMOS LOS MENSAJES Y LLAMAMOS A messageShown
    LaunchedEffect(uiState.productAddedMessage, uiState.errorMessage) {

        // Si hay mensaje de éxito...
        uiState.productAddedMessage?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            cartViewModel.messageShown() // <--- ¡AQUÍ ESTÁ LA LLAMADA QUE FALTABA!
        }

        // Si hay mensaje de error...
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            cartViewModel.messageShown() // <--- ¡AQUÍ TAMBIÉN!
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        modifier = modifier,
        // 3. CONECTAMOS EL SNACKBAR A LA PANTALLA VISUALMENTE
        snackbarHost = { SnackbarHost(snackbarHostState) },

        topBar = {
            CenterAlignedTopAppBar(title = { Text("Mi carrito") })
        },
        bottomBar = {
            if (uiState.items.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.formattedTotalPrice,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = { parentNavController.navigate("checkout") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text("Ir a pagar", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Divider(color = Color(0xFFF0F0F0))

            if (uiState.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.items) { cartItem ->
                        CartItemRow(
                            cartItem = cartItem,
                            onIncrement = { cartViewModel.incrementQuantity(cartItem.product.id) },
                            onDecrement = { cartViewModel.decrementQuantity(cartItem.product.id) },
                            onRemove = { cartViewModel.removeItem(cartItem.product.id) }
                        )
                    }
                }
            }
        }
    }
}

// ... El resto de tus funciones (CartItemRow, QuantityButton) siguen igual abajo ...
@Composable
private fun CartItemRow(
    cartItem: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    // ... Tu código de CartItemRow sigue igual ...
    val product = cartItem.product
    val itemTotalPrice = product.price * cartItem.quantity

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            error = painterResource(android.R.drawable.ic_menu_report_image), // Muestra un triangulo de alerta si falla
            placeholder = painterResource(android.R.drawable.ic_menu_gallery), // Muestra una imagen genérica mientras carga
            modifier = Modifier
                .size(80.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(Color.White)
                .border(1.dp, Color(0xFFF0F0F0), MaterialTheme.shapes.medium),
            contentScale = ContentScale.Fit
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                QuantityButton(icon = Icons.Default.Remove, onClick = onDecrement)
                Text(
                    text = cartItem.quantity.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                QuantityButton(icon = Icons.Default.Add, onClick = onIncrement)
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.height(80.dp)
        ) {
            IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, "Quitar", tint = Color.Gray)
            }
            Text(
                text = "$${itemTotalPrice.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun QuantityButton(icon: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .border(1.dp, Color(0xFFE0E0E0), CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}