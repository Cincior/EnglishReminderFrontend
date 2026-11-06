package pl.cincior.englishreminder.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import pl.cincior.englishreminder.auth.presentation.login.LoginRoute
import pl.cincior.englishreminder.home.presentation.HomeRoute

sealed class Screen(val route: String) {
    data object Login : Screen("Login")
    data object Home : Screen("Home")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        modifier = Modifier
            .statusBarsPadding(),
        navController = navController,
        startDestination = Screen.Login.route

    ) {
        composable(Screen.Login.route) {
           LoginRoute(
               onLoginSuccess = {
                   navController.navigate(Screen.Home.route) {
                       popUpTo(Screen.Login.route) { inclusive = true }
                   }
               }
           )
       }

       composable(Screen.Home.route) {
           HomeRoute(
               onNavigateToLogin = {navController.navigate(Screen.Login.route)}

           )
       }

    }
}