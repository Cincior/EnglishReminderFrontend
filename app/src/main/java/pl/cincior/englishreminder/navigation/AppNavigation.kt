package pl.cincior.englishreminder.navigation

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pl.cincior.englishreminder.auth.presentation.login.LoginRoute
import pl.cincior.englishreminder.auth.presentation.register.RegisterRoute
import pl.cincior.englishreminder.auth.presentation.reset_password.ResetPasswordRoute
import pl.cincior.englishreminder.auth.presentation.reset_request.ResetRequestRoute
import pl.cincior.englishreminder.auth.presentation.verify.VerifyAccountRoute
import pl.cincior.englishreminder.home.presentation.HomeRoute

sealed class Screen(val route: String) {
    data object Login : Screen("Login")
    data object Home : Screen("Home")
    data object Register : Screen("Register")
    data object VerifyAccount : Screen("VerifyAccount?email={email}") {
        fun createRoute(email: String = "") = "VerifyAccount?email=$email"
    }
    data object ResetPasswordRequest : Screen("ResetPasswordRequest?email={email}") {
        fun createRoute(email: String = "") = "ResetPasswordRequest?email=$email"
    }
    data object ResetPassword : Screen("ResetPassword?email={email}") {
        fun createRoute(email: String = "") = "ResetPassword?email=$email"
    }
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
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                },
                onVerifyAccountClick = {
                    navController.navigate(Screen.VerifyAccount.route)
                },
                onForgotPasswordClick = { email ->
                    navController.navigate(Screen.ResetPasswordRequest.createRoute(email))
                }
            )
        }

        composable(Screen.Home.route) {
            HomeRoute(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterRoute(
                onBackClicked = { navController.popBackStack() },
                onRegisterSuccess = { email ->
                    navController.navigate(Screen.VerifyAccount.createRoute(email)) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            Screen.VerifyAccount.route,
            arguments = listOf(navArgument("email") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) {
            VerifyAccountRoute(
                onBackClicked = { navController.popBackStack() },
                onVerifySuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.VerifyAccount.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.ResetPasswordRequest.route,
            arguments = listOf(navArgument("email") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) {
            ResetRequestRoute(
                onBackClicked = { navController.popBackStack() },
                onRequestSuccess = { email ->
                    navController.navigate(Screen.ResetPassword.createRoute(email)) {
                        popUpTo(Screen.ResetPasswordRequest.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(navArgument("email") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) {
            ResetPasswordRoute(
                onBackClicked = { navController.popBackStack() },
                onResetSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ResetPassword.route) { inclusive = true }
                    }
                }
            )
        }
    }
}