package com.example.almacercaapp.ui.theme.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.example.almacercaapp.viewmodel.AuthViewModel
import com.example.almacercaapp.ui.theme.component.HeaderLogo
import com.example.almacercaapp.ui.theme.component.PrimaryButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.runtime.collectAsState
import com.example.almacercaapp.model.UserRole // <-- IMPORTACIÓN CORREGIDA
import com.example.almacercaapp.navigation.Routes

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: AuthViewModel // Recibe el ViewModel
) {
    val user = viewModel.user.value
    var passwordVisible by remember { mutableStateOf(false) }
    val loginSuccess by viewModel.loginSuccess.collectAsState()


    LaunchedEffect(key1 = loginSuccess) {
        if (loginSuccess) {
            val userRole = viewModel.loggedInUserRole.value
            val destination = when (userRole) {
                UserRole.BUYER -> "main_screen"
                UserRole.ADMIN -> "admin_dashboard_screen"
                null -> "signin_method" // Ruta de fallback
            }
            navController.navigate(destination) {
                popUpTo(0) { inclusive = true }
            }
            viewModel.onLoginNavigationDone() // Resetea el estado
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            HeaderLogo(modifier = Modifier
                .size(80.dp)
                .padding(top = 8.dp, bottom = 16.dp)
            )

            Text(
                text = "Iniciar Sesión",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))


            if (user.useEmail) {
                OutlinedTextField(
                    value = user.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.emailError.value != null // Muestra borde rojo
                )
                if (viewModel.emailError.value != null) {
                    Text(
                        text = viewModel.emailError.value!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
            } else {
                OutlinedTextField(
                    value = user.phoneNumber,
                    onValueChange = { viewModel.updatePhone(it) },
                    label = { Text("Número telefónico") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.phoneError.value != null // Muestra borde rojo
                )
                if (viewModel.phoneError.value != null) {
                    Text(
                        text = viewModel.phoneError.value!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Contraseña ---
            OutlinedTextField(
                value = user.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.passwordError.value != null // Muestra borde rojo
            )

            if (viewModel.passwordError.value != null) {
                Text(
                    text = viewModel.passwordError.value!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            PrimaryButton(
                text = "Entrar",
                onClick = {
                    viewModel.submitLogin()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = if (user.useEmail) "Usar número telefónico"
                else "Usar correo electrónico",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { viewModel.toggleMethod() }
            )

            Spacer(modifier = Modifier.height(20.dp))


            Row {
                Text("¿No tienes cuenta? ")
                Text(
                    text = "Regístrate",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { navController.navigate(Routes.RoleSelection.route) }                )
            }
        }
    }
}