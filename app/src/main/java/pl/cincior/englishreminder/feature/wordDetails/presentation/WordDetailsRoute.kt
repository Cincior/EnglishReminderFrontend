package pl.cincior.englishreminder.feature.wordDetails.presentation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun WordDetailsRoute(
    viewModel: WordDetailsViewModel = hiltViewModel(),
    onNavigationBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    WordDetailsScreen(
        uiState,
        onNavigationBack
    )
}

@Composable
fun WordDetailsScreen(
    uiState: WordDetailsUiState,
    onNavigationBack: () -> Unit
) {

    if (uiState.isLoading) {
        CircularProgressIndicator()
        return
    }
    if (uiState.error != null) {
        Text(text = uiState.error.asString(), color = MaterialTheme.colorScheme.error)
        return
    }

    Text("Word details screen!")

}