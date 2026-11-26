package pl.cincior.englishreminder.feature.auth.presentation.startup

sealed interface StartupDestination {
    data object Loading : StartupDestination
    data object Home : StartupDestination
    data class Login(val startupError: String = "") : StartupDestination
}

data class StartupUiState(
    val destination: StartupDestination = StartupDestination.Loading
)
