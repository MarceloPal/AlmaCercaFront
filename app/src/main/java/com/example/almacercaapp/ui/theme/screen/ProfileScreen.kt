package com.example.almacercaapp.ui.theme.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.almacercaapp.R
import com.example.almacercaapp.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class) // Necesario para TopAppBar
@Composable
fun ProfileScreen(
    homeNavController: NavController, // Controlador para navegar DENTRO del HomeGraph (con bottom bar)
    parentNavController: NavController // Controlador para navegar FUERA (ej. al login)
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = "Alerta", tint = Color.Red) },
            title = { Text("Cerrar Sesión", fontWeight = FontWeight.Bold) },
            text = { Text("Estos seguro que vos quieres salir") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        // Usamos parentNavController para salir al flujo de login
                        parentNavController.navigate(Routes.SignInMethod.route) {
                            popUpTo(parentNavController.graph.id) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)) // Rojo
                ) {
                    Text("Si, Cerrar Sesión", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("No, Cancelar", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {


        TopAppBar(
            title = {
                Text(
                    text = "Mi Perfil",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp // Mantenemos el tamaño de fuente
                )
            },
            navigationIcon = {
                IconButton(onClick = { homeNavController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                }
            }

        )



        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

        Spacer(modifier = Modifier.height(4.dp))

        // 📋 Opciones del perfil
        ProfileItem("Mis Pedidos", R.drawable.ic_pedidos) {
            // TODO: homeNavController.navigate(Routes.MyOrders.route)
        }
        ProfileItem("Datos Personales", R.drawable.ic_datos) {
            homeNavController.navigate(Routes.PersonalData.route)
        }
        ProfileItem("Dirección", R.drawable.ic_direccion) {
            // TODO: homeNavController.navigate(Routes.Address.route)
        }
        ProfileItem("Métodos de Pago", R.drawable.ic_pago) {
            // TODO: homeNavController.navigate(Routes.PaymentMethods.route)
        }
        ProfileItem("Notificaciones", R.drawable.ic_noti) {
            homeNavController.navigate(Routes.Notifications.route)
        }
        ProfileItem("FAQs", R.drawable.ic_faq) {
            homeNavController.navigate(Routes.Faq.route)
        }
        ProfileItem("Centro de Ayuda", R.drawable.ic_ayuda) {
            homeNavController.navigate(Routes.HelpCenter.route)
        }

        Spacer(modifier = Modifier.weight(1f)) // Empuja hacia abajo


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp), // Espaciado vertical
            contentAlignment = Alignment.Center // Centra el Row
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { showLogoutDialog = true }
                    // Padding interno para hacer el área de click más grande
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logout),
                    contentDescription = "Salir",
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Salir",
                    color = Color(0xFFE53935),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ProfileItem(text: String, iconRes: Int, onClick: () -> Unit) {

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                // Padding aumentado para coincidir con HelpCenter
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = Color.Black,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, fontSize = 15.sp)
        }


        Divider(color = Color(0xFFE0E0E0), thickness = 0.8.dp)
    }
}