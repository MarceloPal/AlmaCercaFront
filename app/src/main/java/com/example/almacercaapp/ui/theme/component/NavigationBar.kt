package com.example.almacercaapp.ui.theme.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.almacercaapp.R
import androidx.compose.animation.animateColorAsState

// --- Datos de los items de navegación ---
private data class NavItem(
    val label: String,
    val route: String,
    val iconRes: Int
)

private val navItems = listOf(
    NavItem("Tiendas", "home", R.drawable.ic_tiendas_inicio),
    NavItem("Explorar", "explore", R.drawable.ic_explorar),
    NavItem("Carrito", "cart", R.drawable.ic_carrito),
    NavItem("Favoritos", "favorites", R.drawable.ic_favoritos),
    NavItem("Cuenta", "profile", R.drawable.ic_cuenta)
)
// --- Fin de datos ---


@Composable
fun NavigationBar(
    selectedDestination: String,
    onItemSelected: (String) -> Unit
) {
    val darkGreenColor = Color(0xFF2E7D32)
    val unselectedColor = Color.White.copy(alpha = 0.7f)

    // Contenedor principal de la barra de navegación
    NavigationBar(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        containerColor = darkGreenColor,
        tonalElevation = 8.dp // Mantenemos la elevación
    ) {
        // Fila para distribuir los items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp), // Altura estándar de NavigationBar
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Iteramos sobre nuestros items de navegación
            navItems.forEach { item ->
                val isSelected = selectedDestination == item.route
                CustomNavItem(
                    item = item,
                    isSelected = isSelected,
                    selectedColor = darkGreenColor, // Verde para el texto/icono seleccionado
                    unselectedColor = unselectedColor, // Blanco para el no seleccionado
                    onClick = { onItemSelected(item.route) }
                )
            }
        }
    }
}

/**
 * Composable de item personalizado que se anima horizontalmente.
 */
@Composable
private fun CustomNavItem(
    item: NavItem,
    isSelected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit
) {
    // Animamos el color del fondo (pill)
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Transparent,
        label = "bgColor"
    )

    // Animamos el color del contenido (icono y texto)
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedColor,
        label = "contentColor"
    )

    // Usamos Surface para el fondo "pill" y el click
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50), // Forma de pill
        color = backgroundColor,
        contentColor = contentColor,
        modifier = Modifier
            .height(48.dp) // Altura fija para el pill
    ) {
        // Row para el contenido (Icono + Texto)
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp), // Padding horizontal dentro del pill
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Icono
            Icon(
                painter = painterResource(id = item.iconRes),
                contentDescription = item.label,
                modifier = Modifier.size(24.dp)
            )

            // Texto animado
            AnimatedVisibility(
                visible = isSelected,
                // Animación de entrada: aparece y se expande horizontalmente
                enter = fadeIn(initialAlpha = 0.3f) + expandHorizontally(),
                // Animación de salida: se contrae y desaparece
                exit = fadeOut() + shrinkHorizontally()
            ) {
                // Ponemos el Spacer y el Text dentro de un Row
                // para que AnimatedVisibility los trate como un solo bloque
                Row {
                    Spacer(Modifier.width(8.dp)) // Espacio entre icono y texto
                    Text(
                        text = item.label,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}