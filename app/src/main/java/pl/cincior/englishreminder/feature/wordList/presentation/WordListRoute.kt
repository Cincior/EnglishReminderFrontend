package pl.cincior.englishreminder.feature.wordList.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun WordListRoute(
    viewModel: WordListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    WordListScreen(
        uiState = uiState,
        onWordClick = { /* Handle word click */ }
    )
}

@Composable
fun WordListScreen(
    uiState: WordListUiState,
    onWordClick: (String) -> Unit
) {
    Text("Hejka wordlist")
    if (uiState.isLoading) {
        Text("Loading...")
    }
    if (uiState.error != null) {
        Text(uiState.error.asString())
    }
    if (uiState.wordList.isNotEmpty()) {
        uiState.wordList.forEach { userWord ->
            Text(userWord.word)
        }
    }
    // wrodlista osobno, word osobno - rozne composable rozdzielym to?
}