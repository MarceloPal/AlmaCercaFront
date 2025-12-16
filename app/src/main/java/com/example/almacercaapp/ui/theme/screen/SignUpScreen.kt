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
import com.example.almacercaapp.navigation.Routes
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.example.almacercaapp.model.UserRole


@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: AuthViewModel, // Recibe el VM desde NavGraph
    selectedRole: UserRole // Recibe el rol seleccionado desde RoleSelectionScreen
) {
    val user = viewModel.user.value
    var passwordVisible by remember { mutableStateOf(false) }
    val registerSuccess by viewModel.registerSuccess.collectAsState()


    LaunchedEffect(key1 = selectedRole) {
        viewModel.updateRole(selectedRole) // Actualiza el rol en el ViewModel
    }


    LaunchedEffect(key1 = registerSuccess) {
        if (registerSuccess) {
            navController.navigate(Routes.Verification.route) {
            }
            viewModel.onRegisterNavigationDone()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp, vertical = 32.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // ... (HeaderLogo, Text("Registrarse"), Campos como estaban) ...
            HeaderLogo(modifier = Modifier
                .size(80.dp)
                .padding(top = 8.dp, bottom = 16.dp)
            )
            Text(
                text = "Registrarse (${if (selectedRole == UserRole.BUYER) "Comprador" else "Vendedor"})", // Muestra el rol
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // nombire
            OutlinedTextField(
                value = user.username,
                onValueChange = { viewModel.updateUsername(it) },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.nameError.value != null
            )
            if (viewModel.nameError.value != null) {
                Text(
                    text = viewModel.nameError.value!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // correo o telefo
            if (user.useEmail) {
                OutlinedTextField(
                    value = user.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.emailError.value != null
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
                    isError = viewModel.phoneError.value != null
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

            Spacer(modifier = Modifier.height(8.dp))

            // contra
            OutlinedTextField(
                value = user.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon =
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.passwordError.value != null
            )
            if (viewModel.passwordError.value != null) {
                Text(
                    text = viewModel.passwordError.value!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp)) // Espacio antes del botón



            PrimaryButton(
                text = "Registrarse",
                onClick = {
                    viewModel.submitSignUp() // El ViewModel ya sabe el rol
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (user.useEmail) "Usar número telefónico"
                else "Usar correo electrónico",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { viewModel.toggleMethod() }
            )
        }
    }
}