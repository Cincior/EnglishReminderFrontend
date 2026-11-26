package pl.cincior.englishreminder.feature.auth.presentation.startup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun StartupRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: (startupError: String) -> Unit,
    viewModel: StartupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.destination) {
        when (val destination = uiState.destination) {
            StartupDestination.Loading -> Unit
            StartupDestination.Home -> onNavigateToHome()
            is StartupDestination.Login -> onNavigateToLogin(destination.startupError)
        }
    }

    StartupScreen()
}

@Composable
private fun StartupScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
