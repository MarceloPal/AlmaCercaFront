package com.example.almacercaapp.ui.theme.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.almacercaapp.R
import com.example.almacercaapp.model.ProductDto
import com.example.almacercaapp.viewmodel.AdminViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.automirrored.filled.ExitToApp // O Icons.Default.ExitToApp dependiendo de tu versión
import androidx.compose.material3.NavigationDrawerItemDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onLogout: () -> Unit // 1. Nuevo parámetro para la navegación
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf("Gestión Productos") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminDrawerContent(
                currentScreen = currentScreen,
                onScreenSelected = { screen ->
                    currentScreen = screen
                    scope.launch { drawerState.close() }
                },
                onLogout = { // 2. Conectamos el botón con la acción
                    scope.launch {
                        drawerState.close()
                        onLogout()
                    }
                }
            )
        }
    ) {
        AdminScaffoldContent(
            viewModel = viewModel,
            currentScreen = currentScreen,
            onMenuClick = { scope.launch { drawerState.open() } }
        )
    }
}

@Composable
private fun AdminDrawerContent(
    currentScreen: String,
    onScreenSelected: (String) -> Unit,
    onLogout: () -> Unit // 3. Recibimos la función aquí
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight() // Ocupa todo el alto para empujar el botón al fondo
        ) {
            Text(
                "Almacerca Admin",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // --- Opciones Normales ---
            NavigationDrawerItem(
                label = { Text("Dashboard") },
                selected = currentScreen == "Dashboard",
                onClick = { onScreenSelected("Dashboard") },
                icon = { Icon(Icons.Default.Dashboard, null) }
            )
            NavigationDrawerItem(
                label = { Text("Gestión Productos") },
                selected = currentScreen == "Gestión Productos",
                onClick = { onScreenSelected("Gestión Productos") },
                icon = { Icon(Icons.Default.Inventory, null) }
            )

            // --- Empujamos todo lo que sigue hacia abajo ---
            Spacer(modifier = Modifier.weight(1f))

            // --- Botón de Salir ---
            Divider(modifier = Modifier.padding(vertical = 16.dp))

            NavigationDrawerItem(
                label = { Text("Cerrar Sesión") },
                selected = false,
                onClick = onLogout,
                icon = {
                    // Usamos ExitToApp. Si te da error, usa Icons.Default.ArrowBack como alternativa rápida
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
                },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                    unselectedTextColor = MaterialTheme.colorScheme.error,
                    unselectedIconColor = MaterialTheme.colorScheme.error
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminScaffoldContent(viewModel: AdminViewModel, currentScreen: String, onMenuClick: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }
    var showBottomSheet by remember { mutableStateOf(false) }
    val uiStatus by viewModel.uiStatus.collectAsState()

    LaunchedEffect(uiStatus) {
        uiStatus?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            if (it.contains("éxito")) {
                showBottomSheet = false
            }
            viewModel.uiStatus.value = null
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(currentScreen) }, navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, "Abrir menú") } }) },
        floatingActionButton = {
            if (currentScreen == "Gestión Productos") {
                FloatingActionButton(onClick = {
                    viewModel.resetProductFields() // Limpiar campos para un nuevo producto
                    showBottomSheet = true
                }) { Icon(Icons.Default.Add, "Añadir Producto") }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (currentScreen) {
                "Dashboard" -> DashboardHomeScreen()
                "Gestión Productos" -> ProductManagementScreen(
                    viewModel = viewModel,
                    onEditClick = {
                        viewModel.loadProductForEdit(it)
                        showBottomSheet = true
                    },
                    onDeleteClick = { viewModel.deleteProduct(it) }
                )
            }

            if (showBottomSheet) {
                ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState) {
                    CreateOrEditProductForm(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ProductManagementScreen(viewModel: AdminViewModel, onEditClick: (String) -> Unit, onDeleteClick: (ProductDto) -> Unit) {
    val products by viewModel.products.collectAsState()

    if (products.isEmpty()) {
        ComingSoonScreen("No hay productos", "Crea el primero usando el botón '+'")
    } else {
        ProductList(products = products, onEditClick = onEditClick, onDeleteClick = onDeleteClick)
    }
}

@Composable
fun ProductList(products: List<ProductDto>, onEditClick: (String) -> Unit, onDeleteClick: (ProductDto) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products, key = { it.id!! }) { product ->
            AdminProductCard(product = product, onEditClick = { onEditClick(product.id!!) }, onDeleteClick = { onDeleteClick(product) })
        }
    }
}

@Composable
fun AdminProductCard(product: ProductDto, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(product.imageUrl).crossfade(true).error(R.drawable.placeholder_image).build(),
                contentDescription = product.name,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Stock: ${product.stock}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Delete, contentDescription = "Eliminar") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrEditProductForm(viewModel: AdminViewModel) {
    val name by viewModel.productName.collectAsState()
    val description by viewModel.productDescription.collectAsState()
    val price by viewModel.productPrice.collectAsState()
    val stock by viewModel.productStock.collectAsState()
    val imageUrl by viewModel.productImageUrl.collectAsState()
    val editingProductId by viewModel.editingProductId.collectAsState()

    // --- Estados para el selector de categoría ---
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 32.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(if (editingProductId != null) "Editar Producto" else "Nuevo Producto", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = name, onValueChange = { viewModel.productName.value = it }, label = { Text("Nombre del producto") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = description, onValueChange = { viewModel.productDescription.value = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        // --- Selector de Categoría ---
        ExposedDropdownMenuBox(expanded = isDropdownExpanded, onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }) {
            OutlinedTextField(
                value = selectedCategory?.name ?: "",
                onValueChange = {}, // No se cambia directamente
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            viewModel.selectedCategory.value = category
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = price, onValueChange = { viewModel.productPrice.value = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = stock, onValueChange = { viewModel.productStock.value = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = imageUrl, onValueChange = { viewModel.productImageUrl.value = it }, label = { Text("URL de la imagen") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { viewModel.onSaveProduct() }, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Text(if (editingProductId != null) "Guardar Cambios" else "Crear Producto")
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DashboardHomeScreen() {
    ComingSoonScreen("Dashboard en Construcción", "Próximamente: estadísticas de ventas y más.")
}

@Composable
fun ComingSoonScreen(title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Construction, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Text(subtitle, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
    }
}
