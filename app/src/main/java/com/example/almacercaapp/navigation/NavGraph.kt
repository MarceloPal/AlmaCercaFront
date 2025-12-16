package com.example.almacercaapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.almacercaapp.model.UserRole
import com.example.almacercaapp.ui.theme.screen.*
import com.example.almacercaapp.viewmodel.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    adminViewModel: AdminViewModel,
    categoryProductsViewModel: CategoryProductsViewModel,
    favoritesViewModel: FavoritesViewModel,
    productDetailViewModel: ProductDetailViewModel,
    cartViewModel: CartViewModel,
    checkoutViewModel: CheckoutViewModel // <-- AÑADIDO
) {

    val fadeAnimation = tween<Float>(400)

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route,
        enterTransition = { fadeIn(animationSpec = fadeAnimation) },
        exitTransition = { fadeOut(animationSpec = fadeAnimation) },
        popEnterTransition = { fadeIn(animationSpec = fadeAnimation) },
        popExitTransition = { fadeOut(animationSpec = fadeAnimation) }
    ) {

        composable("admin_dashboard_screen") {
            AdminDashboardScreen(
                viewModel = adminViewModel,
                onLogout = {
                    // Navega al login y borra la historia para que no puedan volver atrás
                    navController.navigate(Routes.SignIn.route) {
                        popUpTo("admin_dashboard_sreen") { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Splash.route) { SplashScreen(navController) }
        composable(Routes.Onboarding.route) { OnboardingScreen(navController) }
        composable(Routes.RoleSelection.route) { RoleSelectionScreen(navController) }

        composable(
            route = Routes.SignUp.route,
            arguments = listOf(navArgument("userRole") { type = NavType.EnumType(UserRole::class.java) })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getSerializable("userRole") as? UserRole ?: UserRole.BUYER
            SignUpScreen(navController, authViewModel, role)
        }

        composable(Routes.Verification.route) { VerificationScreen(navController, authViewModel) }
        composable(Routes.Location.route) { LocationScreen() }
        composable(Routes.SignIn.route) { SignInScreen(navController, authViewModel) }
        composable(Routes.SignInMethod.route) { SignInMethodScreen(navController) }

        composable(
            route = "main_screen?start_destination={start_destination}",
            arguments = listOf(navArgument("start_destination") { type = NavType.StringType; nullable = true; defaultValue = null })
        ) { backStackEntry ->
            val startDestination = backStackEntry.arguments?.getString("start_destination")
            MainScreen(
                parentNavController = navController,
                startDestination = startDestination,
                cartViewModel = cartViewModel,
                favoritesViewModel = favoritesViewModel
            )
        }

        composable("details/{storeId}") { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")
            StoreDetailScreen(storeId, { navController.popBackStack() }, navController)
        }

        composable(
            route = "products/{storeId}/{productCategoryId}",
            arguments = listOf(
                navArgument("storeId") { type = NavType.StringType },
                navArgument("productCategoryId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")
            val categoryId = backStackEntry.arguments?.getString("productCategoryId")
            CategoryProductsScreen(storeId, categoryId, categoryProductsViewModel, { navController.popBackStack() }, navController)
        }

        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ProductDetailScreen(
                productId = productId,
                viewModel = productDetailViewModel,
                onBack = { navController.popBackStack() },
                onGoToCart = { navController.navigate("main_screen?start_destination=cart") { popUpTo(navController.graph.startDestinationId) } })
        }

        composable("checkout") {
            CheckoutScreen(
                onBack = { navController.popBackStack() },
                onNavigateToProcessing = {
                    navController.navigate("processing_order") { popUpTo("home") }
                },
                checkoutViewModel = checkoutViewModel
            )
        }
        composable("processing_order") { ProcessingScreen { navController.navigate("order_ready") { popUpTo("home") } } }
        composable("order_ready") { OrderReadyScreen { navController.navigate("main_screen?start_destination=home") { popUpTo("main_screen") { inclusive = true } } } }
    }
}