package com.example.almacercaapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
// Importa todas las pantallas que usará este gráfico
import com.example.almacercaapp.ui.theme.screen.CartScreen
import com.example.almacercaapp.ui.theme.screen.ExploreScreen
import com.example.almacercaapp.ui.theme.screen.FavoritesScreen
import com.example.almacercaapp.ui.theme.screen.HomeScreen

import com.example.almacercaapp.ui.theme.screen.ProfileScreen
import com.example.almacercaapp.ui.theme.screen.FaqScreen
import com.example.almacercaapp.ui.theme.screen.HelpCenterScreen
import com.example.almacercaapp.ui.theme.screen.NotificationsScreen
import com.example.almacercaapp.ui.theme.screen.PersonalDataScreen
import com.example.almacercaapp.ui.theme.screen.SupportChatScreen
import com.example.almacercaapp.viewmodel.CartViewModel
import com.example.almacercaapp.viewmodel.FavoritesViewModel

//Gestiona la navegacion interna entre las 5 pestañas del navigationbar
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeNavGraph(
    navController: NavHostController,
    parentNavController: NavHostController,
    cartViewModel: CartViewModel,
    favoritesViewModel: FavoritesViewModel,
    modifier: Modifier = Modifier,
    startDestination: String
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination
    ) {

        composable(route = "home") {
            HomeScreen(parentNavController = parentNavController)
        }
        composable(route = "explore") {
            ExploreScreen()
        }
        composable(route = "cart") {
            CartScreen(
                parentNavController = parentNavController,
                cartViewModel = cartViewModel
            )
        }
        composable(route = "favorites") {
            FavoritesScreen(
                parentNavController = parentNavController,
                viewModel = favoritesViewModel
            )
        }

        // ProfileScreen necesita AMBOS controladores
        composable(route = "profile") {
            ProfileScreen(
                homeNavController = navController,
                parentNavController = parentNavController
            )
        }

        // El resto de las pantallas usa el navController de las pestañas
        composable(Routes.PersonalData.route) {
            PersonalDataScreen(navController = navController)
        }
        composable(Routes.Notifications.route) {
            NotificationsScreen(navController = navController)
        }
        composable(Routes.Faq.route) {
            FaqScreen(navController = navController)
        }
        composable(Routes.HelpCenter.route) {
            HelpCenterScreen(navController = navController)
        }
        composable(Routes.Support.route) {
            SupportChatScreen(navController = navController)
        }
    }
}