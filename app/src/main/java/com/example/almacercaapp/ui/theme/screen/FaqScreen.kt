package com.example.almacercaapp.ui.theme.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.almacercaapp.ui.theme.GreenPrimary

data class FaqItemData(
    val title: String,
    val content: String
)

val generalFaqs = listOf(
    FaqItemData("¿Cómo hacer una compra?", "Cuando encuentres un producto que quieras comprar, tócalo para ver los detalles. Revisa el precio, la descripción y las opciones compatibles (si las hay), y luego toca el botón 'Agregar al carrito'. Sigue las instrucciones en pantalla para completar tu compra, incluyendo ingresar tus datos de envío y la información de pago."),
    FaqItemData("¿Qué métodos de pago se aceptan?", "Aceptamos tarjetas de crédito, débito y otros métodos de pago locales."),
    FaqItemData("¿Cómo puedo rastrear mis pedidos?", "Puedes rastrear tus pedidos desde la sección 'Mis Pedidos' en tu perfil."),
    FaqItemData("¿Puedo cancelar o devolver un pedido?", "Depende del estado del pedido. Revisa nuestra política de cancelación en el Centro de Ayuda."),
    FaqItemData("¿Cómo puedo contactar al servicio de atención al cliente para recibir ayuda?", "Puedes contactarnos a través del Centro de Ayuda, usando el chat de Soporte."),
    FaqItemData("¿Cómo puedo crear una cuenta?", "Puedes crear una cuenta usando tu correo electrónico o número de teléfono desde la pantalla de registro.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("General", "Cuenta", "Servicio", "Pagos")
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = { Text("FAQs", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                }
            },
            actions = {
                IconButton(onClick = { /* ir a notificaciones */ }) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                }
            }
        )


        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(tabs.size) { index ->
                val title = tabs[index]
                val isSelected = selectedTabIndex == index

                val containerColor = if (isSelected) GreenPrimary else MaterialTheme.colorScheme.surface
                val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                val border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE0E0E0)) // Borde gris claro

                Surface(
                    onClick = { selectedTabIndex = index },
                    shape = MaterialTheme.shapes.extraLarge, // Muy redondeado
                    color = containerColor,
                    contentColor = contentColor,
                    border = border
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                        fontSize = 15.sp
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search for questions...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            shape = MaterialTheme.shapes.extraLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (selectedTabIndex) {
                0 -> { // General
                    items(generalFaqs.filter { it.title.contains(searchQuery, ignoreCase = true) }) { faq ->
                        FaqExpandableItem(item = faq)
                    }
                }
                1 -> { // Cuenta
                    item { Text("Preguntas de Cuenta (No implementado)", modifier = Modifier.padding(16.dp)) }
                }
                // Añadir más casos para otras pestañas
            }
        }
    }
}

@Composable
fun FaqExpandableItem(item: FaqItemData) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
            .clickable { isExpanded = !isExpanded }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Colapsar" else "Expandir"
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.content,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}