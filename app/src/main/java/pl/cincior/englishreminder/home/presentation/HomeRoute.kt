package pl.cincior.englishreminder.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.flow.StateFlow
import pl.cincior.englishreminder.home.domain.model.UserWord

@Composable
fun HomeRoute(
    onNavigateToLogin: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getLastUserWords()
    }

    HomeScreen(
        onNavigateToLogin,
        uiState
    )
}

@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    uiState: HomeUiState
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(red = 55, green = 122, blue = 22)),
            horizontalAlignment = Alignment.End
        ) {
            Button(
                onClick = onNavigateToLogin
            ) {
                Text("Wyloguj")
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(red = 55, green = 122, blue = 122)),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("XD")
            }
            Column(
                modifier = Modifier
                    .weight(1f),

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Twoje ostatnio dodane słówka",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (uiState.userWords.isEmpty()) {
                            item {
                                Text(text = "Brak słówek do wyświetlenia")
                            }
                        } else {
                            items(uiState.userWords.size) { word ->
                                Text(text = uiState.userWords[word].word)
                                Text(text = uiState.userWords[word].translation)
                                Text(text = "Chance: "+ uiState.userWords[word].memorizeChance)
                            }
                        }
                    }
                }

            }
        }
    }
}