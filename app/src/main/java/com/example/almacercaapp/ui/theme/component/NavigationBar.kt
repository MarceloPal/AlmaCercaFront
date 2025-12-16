package com.example.almacercaapp.ui.theme.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.graphics.graphicsLayer

// --- Datos de los items de navegación (Usando Material Icons) ---
private data class NavItem(
    val label: String,
    val route: String,
    val imageVector: androidx.compose.ui.graphics.vector.ImageVector // Cambiamos a ImageVector
)

private val navItems = listOf(
    NavItem("Tiendas", "home", Icons.Filled.Storefront), // Tiendas
    NavItem("Explorar", "explore", Icons.Filled.Search),     // Explorar (Cambio a Search/Explorar)
    NavItem("Carrito", "cart", Icons.Filled.ShoppingCart), // Carrito
    NavItem("Favoritos", "favorites", Icons.Filled.Favorite), // Favoritos
    NavItem("Cuenta", "profile", Icons.Filled.AccountCircle) // Cuenta/Perfil
)
// --- Fin de datos ---


@Composable
fun NavigationBar(
    selectedDestination: String,
    onItemSelected: (String) -> Unit
) {
    val darkGreenColor = Color(0xFF2E7D32)
    // Usaremos un color más suave para los no seleccionados para mejor contraste
    val unselectedColor = Color.White.copy(alpha = 0.8f)

    // Contenedor principal de la barra de navegación
    NavigationBar(
        // Reducimos el radio de las esquinas para que no se vea tan pesado
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        containerColor = darkGreenColor,
        tonalElevation = 8.dp
    ) {
        // Fila para distribuir los items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp), // Altura ligeramente reducida para compacidad
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Iteramos sobre nuestros items de navegación
            navItems.forEach { item ->
                val isSelected = selectedDestination == item.route
                CustomNavItem(
                    item = item,
                    isSelected = isSelected,
                    selectedColor = darkGreenColor,
                    unselectedColor = unselectedColor,
                    onClick = { onItemSelected(item.route) }
                )
            }
        }
    }
}

/**
 * Composable de item personalizado con animación de pill y escala.
 */
@Composable
private fun CustomNavItem(
    item: NavItem,
    isSelected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit
) {
    // Animación de color de fondo (pill)
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Transparent,
        animationSpec = tween(durationMillis = 300), // Animación más notoria
        label = "bgColor"
    )

    // Animación de color del contenido (icono y texto)
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedColor,
        animationSpec = tween(durationMillis = 300),
        label = "contentColor"
    )

    // Animación de escala (sólo para el icono)
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1.0f, // Icono se agranda
        animationSpec = tween(durationMillis = 300),
        label = "iconScale"
    )

    // Usamos Surface para el fondo "pill" y el click
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = backgroundColor,
        contentColor = contentColor,
        modifier = Modifier
            .height(44.dp) // Altura reducida para ser más estilizado
    ) {
        // Row para el contenido (Icono + Texto)
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp), // Padding horizontal ajustado
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Icono con animación de escala
            Icon(
                imageVector = item.imageVector, // Usamos ImageVector
                contentDescription = item.label,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer { scaleX = scale; scaleY = scale } // Aplicamos la animación de escala
            )

            // Texto animado
            AnimatedVisibility(
                visible = isSelected,
                // Usamos un tween más rápido para la animación del texto
                enter = fadeIn(animationSpec = tween(200)) + expandHorizontally(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(200)) + shrinkHorizontally(animationSpec = tween(200))
            ) {
                Row {
                    Spacer(Modifier.width(6.dp)) // Espacio ligeramente reducido entre icono y texto
                    Text(
                        text = item.label,
                        fontWeight = FontWeight.SemiBold, // Un poco más de peso a la fuente
                        maxLines = 1
                    )
                }
            }
        }
    }
}