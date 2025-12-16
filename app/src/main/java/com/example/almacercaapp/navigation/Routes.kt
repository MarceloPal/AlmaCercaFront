package com.example.almacercaapp.navigation
import com.example.almacercaapp.model.UserRole
//archivo que se asegura que las rutas esten bien escritas
sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object Onboarding : Routes("onboarding")
    object RoleSelection : Routes("role_selection")
    object SignUp : Routes("signup/{userRole}"){
        fun createRoute(role: UserRole) = "signup/$role" // Nueva función para crear la ruta
    }
    object Verification : Routes("verification")
    object Location : Routes("location")
    object SignIn : Routes("signin")
    object SignInMethod : Routes("signin_method")
    object PersonalData : Routes("personal_data")
    object Notifications : Routes("notifications")
    object Faq : Routes("faq")
    object HelpCenter : Routes("help_center")
    object Support : Routes("support_chat")


}

