package pl.cincior.englishreminder.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pl.cincior.englishreminder.core.presentation.components.WordListItem
import java.time.Instant

@Composable
fun HomeRoute(
    onNavigateToLogin: () -> Unit,
    onNavigateToWordDetails: (String) -> Unit,
    onNavigationToWordList: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getLastUserWords()
        viewModel.getNickname()
    }

    HomeScreen(
        onNavigateToLogin,
        onNavigateToWordDetails,
        onNavigationToWordList,
        uiState
    )
}

@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToWordDetails: (String) -> Unit,
    onNavigationToWordList: () -> Unit,
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
                Row() {
                    Column() {
                        Text(
                            text = "Witaj ${uiState.nickName} . Masz ${uiState.userWords.size} słówek do powtórki",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                Row(
                    Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxSize()
                        .height(100.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        Modifier
                            .padding(16.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxWidth(0.5f)
                            .fillMaxHeight(0.5f)
                            .weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clickable(onClick = onNavigationToWordList)
                                .fillMaxSize()
                        ) {
                            Text(
                                text = "Wyświetl słówka",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                    }
                    Column(
                        Modifier
                            .padding(16.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxWidth(0.5f)
                            .fillMaxHeight(0.5f)
                            .weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clickable( onClick = { /* go to add word */ })
                                .fillMaxSize()
                        ) {
                            Text(
                                text = "Dodaj słówko",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                    }
                }
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
                        if (uiState.isLoading) {
                            item {
                                Text(text = "Ładowanie...")
                            }
                        } else if (uiState.userWords.isEmpty()) {
                            item {
                                Text(text = "Brak słówek do wyświetlenia")
                            }
                        } else {
                            items(
                                items = uiState.userWords,
                                key = {word -> word.id}
                            ) { word ->
                                WordListItem(
                                    word.id,
                                    word.word,
                                    word.translation,
                                    word.memorizeChance,
                                    onNavigateToWordDetails = onNavigateToWordDetails
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}