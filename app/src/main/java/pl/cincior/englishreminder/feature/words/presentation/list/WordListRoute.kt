package pl.cincior.englishreminder.feature.words.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pl.cincior.englishreminder.core.presentation.components.WordListItem
import pl.cincior.englishreminder.feature.words.domain.model.WordCategory

@Composable
fun WordListRoute(
    onNavigateToWordDetails: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: WordListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    WordListScreen(
        uiState = uiState,
        onWordClick = onNavigateToWordDetails,
        onBackClick = onBackClick,
        onRefresh = { viewModel.refreshWordsFromBackend() },
        onCategoryChange = { category -> viewModel.onCategoryChange(category) },
        onCategoryDismiss = { viewModel.onCategoryDismiss() },
        onCategoryExpand = { viewModel.onCategoryExpand() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordListScreen(
    uiState: WordListUiState,
    onWordClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onCategoryChange: (WordCategory?) -> Unit,
    onCategoryDismiss: () -> Unit,
    onCategoryExpand: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredWords = remember(uiState.wordList, searchQuery, uiState.category) {
        var currentWords = uiState.wordList

        if (uiState.category != null) {
            currentWords = currentWords.filter { word -> word.category == uiState.category }
        }

        if (searchQuery.isNotBlank()) {
            currentWords = currentWords.filter { word ->
                word.word.contains(searchQuery, ignoreCase = true) ||
                        word.translation.contains(searchQuery, ignoreCase = true)
            }
        }
        currentWords
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Lista słówek",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "${uiState.wordList.size} słówek w bazie",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text(
                            text = "←",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            Box() {
                IconButton(
                    onClick = { onCategoryExpand() },
                ) {
                    Text("Filtruj")
                }
                DropdownMenu(
                    expanded = uiState.isCategoryExpanded,
                    onDismissRequest = { onCategoryDismiss() }
                ) {
                    for (category in WordCategory.entries) {
                        DropdownMenuItem(
                            text = {Text(category.displayName)},
                            onClick = { onCategoryChange(category) }
                        )
                        DropdownMenuItem(
                            text = {Text("Wyczyść filtr")},
                            onClick = { onCategoryChange(null) }
                        )
                    }
                }
            }
        }
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Szukaj słówka...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (uiState.error != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.error.asString(),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            Button(onClick = onRefresh) {
                                Text("Spróbuj ponownie")
                            }
                        }
                    }
                }

                if (filteredWords.isEmpty() && !uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotBlank()) "Brak wyników wyszukiwania" else "Brak słówek na liście",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = filteredWords,
                            key = { word -> word.id }
                        ) { word ->
                            WordListItem(
                                wordId = word.id,
                                word = word.word,
                                translation = word.translation,
                                memorizeChance = word.memorizeChance,
                                onNavigateToWordDetails = onWordClick
                            )
                        }
                    }
                }
            }
        }
    }
}
