package pl.cincior.englishreminder.feature.words.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pl.cincior.englishreminder.core.presentation.components.WordListItem
import pl.cincior.englishreminder.feature.words.domain.model.UserWord
import pl.cincior.englishreminder.feature.words.domain.model.WordCategory

private enum class MemorizeChanceSort {
    NONE,
    ASCENDING,
    DESCENDING
}

@Composable
fun WordListRoute(
    onNavigateToWordDetails: (String) -> Unit,
    onNavigateToCategoryTest: (WordCategory) -> Unit,
    onBackClick: () -> Unit,
    viewModel: WordListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    WordListScreen(
        uiState = uiState,
        onWordClick = onNavigateToWordDetails,
        onCategoryTestClick = onNavigateToCategoryTest,
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
    onCategoryTestClick: (WordCategory) -> Unit,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onCategoryChange: (WordCategory?) -> Unit,
    onCategoryDismiss: () -> Unit,
    onCategoryExpand: () -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var chanceSortOrder by rememberSaveable { mutableStateOf(MemorizeChanceSort.NONE) }
    var isSortMenuExpanded by remember { mutableStateOf(false) }

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
    val visibleWords = remember(filteredWords, chanceSortOrder) {
        when (chanceSortOrder) {
            MemorizeChanceSort.NONE -> filteredWords
            MemorizeChanceSort.ASCENDING -> filteredWords.sortedWith(
                compareBy<UserWord> { it.memorizeChance < 0f }
                    .thenBy { it.memorizeChance }
            )
            MemorizeChanceSort.DESCENDING -> filteredWords.sortedWith(
                compareBy<UserWord> { it.memorizeChance < 0f }
                    .thenByDescending { it.memorizeChance }
            )
        }
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
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Text("×", style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        FilledTonalButton(
                            onClick = onCategoryExpand,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.category?.displayName ?: "Kategoria",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
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
                            }
                            DropdownMenuItem(
                                text = { Text("Wyczyść filtr") },
                                onClick = { onCategoryChange(null) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        FilledTonalButton(
                            onClick = { isSortMenuExpanded = true },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = when (chanceSortOrder) {
                                    MemorizeChanceSort.NONE -> "Szansa"
                                    MemorizeChanceSort.ASCENDING -> "Szansa ↑"
                                    MemorizeChanceSort.DESCENDING -> "Szansa ↓"
                                },
                                maxLines = 1
                            )
                        }
                        DropdownMenu(
                            expanded = isSortMenuExpanded,
                            onDismissRequest = { isSortMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Szansa rosnąco") },
                                onClick = {
                                    chanceSortOrder = MemorizeChanceSort.ASCENDING
                                    isSortMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Szansa malejąco") },
                                onClick = {
                                    chanceSortOrder = MemorizeChanceSort.DESCENDING
                                    isSortMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Domyślna kolejność") },
                                onClick = {
                                    chanceSortOrder = MemorizeChanceSort.NONE
                                    isSortMenuExpanded = false
                                }
                            )
                        }
                    }
                    Text(
                        text = "${visibleWords.size} z ${uiState.wordList.size}",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                uiState.category?.let { category ->
                    val categoryWordCount = uiState.wordList.count { it.category == category }
                    Button(
                        onClick = { onCategoryTestClick(category) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        enabled = categoryWordCount > 0
                    ) {
                        Text("Rozpocznij test ($categoryWordCount)")
                    }
                }

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
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when {
                                searchQuery.isNotBlank() -> "Nie znaleziono słówek pasujących do wyszukiwania"
                                uiState.category != null -> "Brak słówek w tej kategorii"
                                else -> "Twoja lista jest jeszcze pusta"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = visibleWords,
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
