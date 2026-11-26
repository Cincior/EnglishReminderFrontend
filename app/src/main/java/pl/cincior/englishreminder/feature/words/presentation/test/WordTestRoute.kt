package pl.cincior.englishreminder.feature.words.presentation.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pl.cincior.englishreminder.R
import pl.cincior.englishreminder.core.presentation.components.ErrorCard
import java.util.Locale

@Composable
fun WordTestRoute(
    onNavigateBack: () -> Unit,
    viewModel: WordTestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(uiState.isSubmitting) {
        if (uiState.isSubmitting) keyboardController?.hide()
    }

    WordTestScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onAnswerChange = viewModel::onAnswerChanged,
        onCheckAnswer = {
            keyboardController?.hide()
            viewModel.checkAnswer()
        },
        onNextWord = viewModel::moveToNextWord,
        onPronounce = viewModel::pronounceWord
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WordTestScreen(
    uiState: WordTestUiState,
    onNavigateBack: () -> Unit,
    onAnswerChange: (String) -> Unit,
    onCheckAnswer: () -> Unit,
    onNextWord: () -> Unit,
    onPronounce: () -> Unit
) {
    val currentWord = uiState.currentWord
    val hasAnswerResult = uiState.result != null || uiState.isTimedOut
    val canAnswer = currentWord != null && !hasAnswerResult
    val buttonText = when {
        hasAnswerResult && uiState.isCategoryTest && !uiState.isLastWord -> "Następne słówko"
        hasAnswerResult && uiState.isCategoryTest -> "Zakończ test"
        hasAnswerResult -> "Wróć do szczegółów"
        uiState.error != null && currentWord != null -> "Spróbuj ponownie"
        else -> "Sprawdź"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.category?.let { "Test: ${it.displayName}" } ?: "Test słówka",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    Text(
                        text = "00:%02d".format(uiState.secondsRemaining),
                        modifier = Modifier.padding(end = 16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.secondsRemaining <= 5) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            currentWord == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    uiState.error?.let { ErrorCard(error = it) }
                    Button(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
                        Text("Wróć")
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Słówko ${uiState.currentIndex + 1} z ${uiState.words.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = currentWord.word.uppercase(Locale.ROOT),
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = onPronounce) {
                                androidx.compose.foundation.Image(
                                    painter = painterResource(R.drawable.sound),
                                    contentDescription = "Odtwórz wymowę",
                                    modifier = Modifier.size(24.dp),
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.answer,
                        onValueChange = onAnswerChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Wpisz tłumaczenie") },
                        enabled = canAnswer && !uiState.isSubmitting,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onCheckAnswer() })
                    )

                    uiState.error?.let { ErrorCard(error = it) }
                    uiState.speechError?.let { ErrorCard(error = it) }

                    if (hasAnswerResult) {
                        val correct = uiState.result?.isCorrect == true
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (correct) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.errorContainer
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (correct) "Dobrze!" else "Niestety, źle.",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Poprawne tłumaczenie: ${currentWord.translation}")
                                uiState.submittedTimeSeconds?.let { seconds ->
                                    Text(
                                        "Czas odpowiedzi: ${
                                            String.format(Locale.getDefault(), "%.1f", seconds)
                                        } s"
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            if (!hasAnswerResult) {
                                onCheckAnswer()
                            } else if (uiState.isCategoryTest && !uiState.isLastWord) {
                                onNextWord()
                            } else {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSubmitting
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(buttonText)
                        }
                    }
                }
            }
        }
    }
}
