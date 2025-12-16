package com.example.almacercaapp.ui.theme.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ProcessingScreen(
    onOrderProcessed: () -> Unit
) {
    // Simula una carga de 3 segundos
    LaunchedEffect(key1 = true) {
        delay(3000L) // 3 segundos
        onOrderProcessed() // Navega a la pantalla final
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // (Aquí podrías poner tu GIF en lugar del indicador)
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
            Text(
                text = "Estamos procesando tu compra...",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}