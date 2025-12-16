package com.example.almacercaapp.ui.theme.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.example.almacercaapp.R
import com.example.almacercaapp.ui.theme.GreenPrimary
import com.example.almacercaapp.navigation.Routes


@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(2000) // Espera 2 segundos antes de navegar
        navController.navigate(Routes.Onboarding.route) {
            popUpTo(Routes.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary), // Color verde del fondo
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // Reemplaza con tu logo
            contentDescription = "Logo",
            modifier = Modifier.size(180.dp)
        )
    }
}