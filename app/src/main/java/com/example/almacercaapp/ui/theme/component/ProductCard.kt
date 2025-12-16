package com.example.almacercaapp.ui.theme.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.* // Importa todo lo de Material3
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.almacercaapp.R
import com.example.almacercaapp.model.Product

/**
 * Componente reutilizable para mostrar un producto en una tarjeta.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: Product,
    onProductClick: () -> Unit
) {
    Card(
        onClick = onProductClick, // Al hacer clic en la TARJETA, vas al detalle
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.width(160.dp)
    ) {
        Column {
            // Imagen del producto
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                placeholder = painterResource(id = R.drawable.placeholder_image), // Asegúrate de tener esta imagen o quita la línea
                error = painterResource(id = R.drawable.placeholder_image),
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format("$%.0f", product.price),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )

                    // --- CAMBIO IMPORTANTE AQUÍ ---
                    // Usamos FilledIconButton para que parezca un botón real
                    FilledIconButton(
                        onClick = { onProductClick() },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary, // Fondo VERDE
                            contentColor = Color.White // Icono BLANCO
                        )
                    ) {
                        // Usamos el icono estándar de "+"
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Añadir al carro",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    // -----------------------------
                }
            }
        }
    }
}