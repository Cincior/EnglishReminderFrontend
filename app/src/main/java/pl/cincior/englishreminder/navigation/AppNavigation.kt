package pl.cincior.englishreminder.navigation

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import pl.cincior.englishreminder.feature.auth.presentation.login.LoginRoute
import pl.cincior.englishreminder.feature.auth.presentation.startup.StartupRoute
import pl.cincior.englishreminder.feature.auth.presentation.register.RegisterRoute
import pl.cincior.englishreminder.feature.auth.presentation.reset_password.ResetPasswordRoute
import pl.cincior.englishreminder.feature.auth.presentation.reset_request.ResetRequestRoute
import pl.cincior.englishreminder.feature.auth.presentation.verify.VerifyAccountRoute
import pl.cincior.englishreminder.feature.words.presentation.add.AddWordRoute
import pl.cincior.englishreminder.feature.words.presentation.details.WordDetailsRoute
import pl.cincior.englishreminder.feature.words.presentation.home.HomeRoute
import pl.cincior.englishreminder.feature.words.presentation.list.WordListRoute
import pl.cincior.englishreminder.feature.words.presentation.test.WordTestRoute

sealed interface Screen {
    @Serializable data object Splash : Screen
    @Serializable data class Login(val startupError: String? = null) : Screen
    @Serializable data object Home : Screen
    @Serializable data object Register : Screen
    @Serializable data class VerifyAccount(val email: String? = null) : Screen
    @Serializable data class ResetPasswordRequest(val email: String? = null) : Screen
    @Serializable data class ResetPassword(val email: String? = null) : Screen
    @Serializable data object WordList : Screen
    @Serializable data class WordDetails(val id: String) : Screen
    @Serializable data class WordTest(val wordId: String? = null, val category: String? = null) : Screen
    @Serializable data object WordAdd : Screen
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        modifier = Modifier.statusBarsPadding(),
        navController = navController,
        startDestination = Screen.Splash
    ) {
        composable<Screen.Splash> {
            StartupRoute(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Splash> { inclusive = true }
                    }
                },
                onNavigateToLogin = { startupError ->
                    navController.navigate(Screen.Login(startupError = startupError)) {
                        popUpTo<Screen.Splash> { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Login> { backStackEntry ->
            val loginArgs = backStackEntry.toRoute<Screen.Login>()
            LoginRoute(
                startupError = loginArgs.startupError.orEmpty(),
                onLoginSuccess = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Login> { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register)
                },
                onVerifyAccountClick = {
                    navController.navigate(Screen.VerifyAccount())
                },
                onForgotPasswordClick = { email ->
                    navController.navigate(Screen.ResetPasswordRequest(email = email))
                }
            )
        }

        composable<Screen.Home> {
            HomeRoute(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login()) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                },
                onNavigateToWordDetails = { wordId ->
                    navController.navigate(Screen.WordDetails(id = wordId))
                },
                onNavigationToWordList = {
                    navController.navigate(Screen.WordList)
                },
                onNavigationToAddWord = {
                    navController.navigate(Screen.WordAdd)
                }
            )
        }

        composable<Screen.Register> {
            RegisterRoute(
                onBackClicked = { navController.popBackStack() },
                onRegisterSuccess = { email ->
                    navController.navigate(Screen.VerifyAccount(email = email)) {
                        popUpTo<Screen.Register> { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.VerifyAccount> {
            VerifyAccountRoute(
                onBackClicked = { navController.popBackStack() },
                onVerifySuccess = {
                    navController.navigate(Screen.Login()) {
                        popUpTo<Screen.VerifyAccount> { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.ResetPasswordRequest> {
            ResetRequestRoute(
                onBackClicked = { navController.popBackStack() },
                onRequestSuccess = { email ->
                    navController.navigate(Screen.ResetPassword(email = email)) {
                        popUpTo<Screen.ResetPasswordRequest> { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.ResetPassword> {
            ResetPasswordRoute(
                onBackClicked = { navController.popBackStack() },
                onResetSuccess = {
                    navController.navigate(Screen.Login()) {
                        popUpTo<Screen.ResetPassword> { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.WordList>(
            deepLinks = listOf(navDeepLink { uriPattern = "englishreminder://word_list" })
        ) {
            WordListRoute(
                onNavigateToWordDetails = { wordId ->
                    navController.navigate(Screen.WordDetails(id = wordId))
                },
                onNavigateToCategoryTest = { category ->
                    navController.navigate(Screen.WordTest(category = category.name))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.WordDetails>(
            deepLinks = listOf(navDeepLink { uriPattern = "englishreminder://word_details/{id}" })
        ) {
            WordDetailsRoute(
                onNavigationBack = { navController.popBackStack() },
                onStartTest = { wordId ->
                    navController.navigate(Screen.WordTest(wordId = wordId))
                }
            )
        }

        composable<Screen.WordTest> {
            WordTestRoute(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.WordAdd> {
            AddWordRoute(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
