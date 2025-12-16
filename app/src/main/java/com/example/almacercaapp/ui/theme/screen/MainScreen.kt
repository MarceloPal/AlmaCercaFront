package com.example.almacercaapp.ui.theme.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.almacercaapp.navigation.HomeNavGraph
import com.example.almacercaapp.ui.theme.component.NavigationBar
import androidx.compose.runtime.getValue
import com.example.almacercaapp.viewmodel.CartViewModel
import com.example.almacercaapp.viewmodel.FavoritesViewModel

@Composable
fun MainScreen(
    parentNavController: NavHostController,
    startDestination: String?,
    cartViewModel: CartViewModel,
    favoritesViewModel: FavoritesViewModel
) {
    // Este es el NavController para las pestañas INTERNAS (Home, Carrito, etc.)
    val homeNavController = rememberNavController()

    // Determina la ruta de inicio. Si viene una instrucción (ej: "cart"), úsala.
    // Si no, usa la ruta de "home" por defecto.
    val startRoute = startDestination ?: "home"

    // Observamos la pila de navegación del NavController INTERNO para saber en qué pantalla estamos
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    // La ruta actual para iluminar el ícono correcto en la BottomBar.
    val currentRoute = navBackStackEntry?.destination?.route ?: startRoute

    // Scaffold es el "marco" de la ventana. Solo lo defines UNA VEZ.
    Scaffold(
        bottomBar = {
            NavigationBar(
                selectedDestination = currentRoute,
                onItemSelected = { route ->
                    // Cuando se toca un ítem, navegas a esa ruta
                    homeNavController.navigate(route) {
                        popUpTo(homeNavController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            HomeNavGraph(
                navController = homeNavController,
                parentNavController = parentNavController,
                cartViewModel = cartViewModel,
                favoritesViewModel = favoritesViewModel,
                startDestination = startRoute
            )
        }
    }
}