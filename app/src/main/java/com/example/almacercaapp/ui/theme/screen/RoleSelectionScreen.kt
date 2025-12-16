package com.example.almacercaapp.ui.theme.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.almacercaapp.model.UserRole
import com.example.almacercaapp.navigation.Routes
import com.example.almacercaapp.ui.theme.GreenPrimary
import com.example.almacercaapp.ui.theme.component.HeaderLogo

@Composable
fun RoleSelectionScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Centra todo verticalmente
    ) {
        HeaderLogo(modifier = Modifier.size(120.dp)) // Muestra el logo

        Spacer(modifier = Modifier.height(48.dp)) // Espacio

        Text(
            text = "¿Cómo quieres usar AlmaCerca?",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground // Color del texto según el tema
        )

        Spacer(modifier = Modifier.height(32.dp)) // Espacio


        Button(
            onClick = {
                // Navega a SignUp pasando "BUYER" como argumento
                navController.navigate(Routes.SignUp.createRoute(UserRole.BUYER))
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary), // Color verde
            modifier = Modifier
                .fillMaxWidth() // Ancho completo
                .height(55.dp), // Altura fija
            shape = MaterialTheme.shapes.medium // Forma del botón
        ) {
            Text("Quiero Comprar", color = Color.White, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(40.dp)) // Espacio antes del texto de login

        // --- Texto para Iniciar Sesión ---
        Row {
            Text("¿Ya tienes cuenta? ")
            Text(
                text = "Inicia Sesión",
                color = MaterialTheme.colorScheme.primary, // Color primario del tema
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { // Hace el texto clickeable
                    // Navega a la pantalla de métodos de inicio de sesión
                    navController.navigate(Routes.SignInMethod.route)
                }
            )
        }
    }
}
