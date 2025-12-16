package com.example.almacercaapp.ui.theme.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.almacercaapp.R
import com.example.almacercaapp.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Centro de Ayuda", fontWeight = FontWeight.Bold) },
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

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                HelpItem(
                    text = "Soporte",
                    icon = Icons.Default.Headset,
                    onClick = { navController.navigate(Routes.Support.route) }
                )
            }
            item {
                HelpItem(
                    text = "Whatsapp",
                    icon = Icons.Default.Chat, // Icono genérico
                    onClick = { /* Abrir Whatsapp */ }
                )
            }
            item {
                HelpItem(
                    text = "Website",
                    icon = Icons.Default.Language,
                    onClick = { /* Abrir Website */ }
                )
            }
            item {
                HelpItem(
                    text = "Facebook",
                    iconPainter = R.drawable.ic_facebook, // Usando tu drawable
                    onClick = { /* Abrir Facebook */ }
                )
            }
            item {
                HelpItem(
                    text = "Twitter",
                    icon = Icons.Default.Message, // Icono genérico
                    onClick = { /* Abrir Twitter */ }
                )
            }
            item {
                HelpItem(
                    text = "Instagram",
                    icon = Icons.Default.CameraAlt, // Icono genérico
                    onClick = { /* Abrir Instagram */ }
                )
            }
        }
    }
}

@Composable
fun HelpItem(
    text: String,
    icon: ImageVector? = null,
    iconPainter: Int? = null,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        } else if (iconPainter != null) {
            Icon(
                painter = painterResource(id = iconPainter),
                contentDescription = text,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    Divider()
}