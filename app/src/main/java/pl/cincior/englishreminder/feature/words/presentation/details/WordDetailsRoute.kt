package pl.cincior.englishreminder.feature.words.presentation.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pl.cincior.englishreminder.core.domain.model.DateTime
import pl.cincior.englishreminder.core.presentation.components.AppTextField
import pl.cincior.englishreminder.core.presentation.components.ErrorCard
import pl.cincior.englishreminder.core.presentation.components.LoadingButton
import pl.cincior.englishreminder.core.presentation.components.NotificationDateTimePicker
import pl.cincior.englishreminder.feature.words.domain.model.WordCategory

@Composable
fun WordDetailsRoute(
    viewModel: WordDetailsViewModel = hiltViewModel(),
    onNavigationBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.deleteState.isDeleted) {
        if (uiState.deleteState.isDeleted) {
            onNavigationBack()
        }
    }

    WordDetailsScreen(
        uiState = uiState,
        onNavigationBack = onNavigationBack,
        onToggleTranslationVisibility = viewModel::onToggleTranslationVisibility,
        onCategorySelect = viewModel::onCategorySelected,
        onTestAnswerChange = viewModel::onTestAnswerChanged,
        onTestWordClick = viewModel::testWord,
        onToggleNotificationPicker = viewModel::onToggleNotificationPicker,
        onScheduleDateChanged = viewModel::onScheduleDateChanged,
        onScheduleTimeChanged = viewModel::onScheduleTimeChanged,
        onScheduleNotificationClick = viewModel::scheduleNotification,
        onShowEditDialog = viewModel::onShowEditDialog,
        onEditWordChange = viewModel::onEditWordChanged,
        onEditTranslationChange = viewModel::onEditTranslationChanged,
        onUpdateWordClick = viewModel::updateWord,
        onShowDeleteConfirmation = viewModel::onShowDeleteConfirmation,
        onDeleteWordClick = viewModel::deleteWord
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailsScreen(
    uiState: WordDetailsUiState,
    onNavigationBack: () -> Unit,
    onToggleTranslationVisibility: () -> Unit,
    onCategorySelect: (WordCategory) -> Unit,
    onTestAnswerChange: (String) -> Unit,
    onTestWordClick: () -> Unit,
    onToggleNotificationPicker: (Boolean) -> Unit,
    onScheduleDateChanged: (Long?) -> Unit,
    onScheduleTimeChanged: (hour: Int, minute: Int) -> Unit,
    onScheduleNotificationClick: () -> Unit,
    onShowEditDialog: (Boolean) -> Unit,
    onEditWordChange: (String) -> Unit,
    onEditTranslationChange: (String) -> Unit,
    onUpdateWordClick: () -> Unit,
    onShowDeleteConfirmation: (Boolean) -> Unit,
    onDeleteWordClick: () -> Unit
) {
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val testState = uiState.testState
    val notificationState = uiState.notificationState
    val editState = uiState.editState
    val deleteState = uiState.deleteState

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Szczegóły słówka",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigationBack) {
                        Text(
                            text = "←",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                actions = {
                    if (uiState.word != null) {
                        TextButton(onClick = { onShowEditDialog(true) }) {
                            Text("Edytuj")
                        }
                        TextButton(onClick = { onShowDeleteConfirmation(true) }) {
                            Text(
                                text = "Usuń",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
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
        if (uiState.isLoading && uiState.word == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.error != null) {
                ErrorCard(error = uiState.error)
            }

            if (uiState.successMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = uiState.successMessage.asString(),
                        modifier = Modifier.padding(14.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (notificationState.successMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Text(
                        text = notificationState.successMessage.asString(),
                        modifier = Modifier.padding(14.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            val word = uiState.word
            if (word != null) {
                // Karta ze słówkiem
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = word.word,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (uiState.isTranslationVisible) {
                                Text(
                                    text = "Tłumaczenie: ${word.translation}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text(
                                    text = "Tłumaczenie: ••••••••",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Normal
                                )
                            }

                            TextButton(onClick = onToggleTranslationVisibility) {
                                Text(if (uiState.isTranslationVisible) "Ukryj" else "Pokaż")
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Dodano:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = DateTime.formatDateString(word.insertDateTime),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Szansa zapamiętania:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = if (word.memorizeChance >= 0) "${word.memorizeChance}%" else "Brak danych",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Wybór kategorii
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = "Kategoria",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = word.category.displayName,
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = false,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { categoryDropdownExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = categoryDropdownExpanded,
                                    onDismissRequest = { categoryDropdownExpanded = false },
                                    modifier = Modifier.fillMaxWidth(0.85f)
                                ) {
                                    WordCategory.entries.forEach { category ->
                                        DropdownMenuItem(
                                            text = { Text(category.displayName) },
                                            onClick = {
                                                onCategorySelect(category)
                                                categoryDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Sekcja: Sprawdź się
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Sprawdź się",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Wpisz tłumaczenie dla słowa '${word.word}':",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )

                        AppTextField(
                            value = testState.answer,
                            onValueChange = onTestAnswerChange,
                            label = "Twoje tłumaczenie",
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    onTestWordClick()
                                }
                            )
                        )

                        if (testState.error != null) {
                            ErrorCard(error = testState.error)
                        }

                        LoadingButton(
                            text = "Sprawdź",
                            onClick = {
                                keyboardController?.hide()
                                onTestWordClick()
                            },
                            isLoading = testState.isTesting
                        )

                        // Wynik sprawdzania
                        if (testState.result != null) {
                            val isCorrect = testState.result.isCorrect
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCorrect) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = if (isCorrect) "Brawo! Poprawna odpowiedź!" else "Niepoprawna odpowiedź.",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCorrect) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    if (!isCorrect) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Poprawne tłumaczenie to: ${word.translation}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Sekcja: Zaplanuj powiadomienie
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Powiadomienie",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Zaplanuj przypomnienie o powtórce",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }

                            Button(
                                onClick = { onToggleNotificationPicker(!notificationState.showPicker) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(if (notificationState.showPicker) "Ukryj" else "Zaplanuj")
                            }
                        }

                        AnimatedVisibility(visible = notificationState.showPicker) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                NotificationDateTimePicker(
                                    dateTime = notificationState.dateTime,
                                    onDateChanged = onScheduleDateChanged,
                                    onTimeChanged = onScheduleTimeChanged
                                )

                                if (notificationState.error != null) {
                                    ErrorCard(error = notificationState.error)
                                }

                                LoadingButton(
                                    text = "Zapisz powiadomienie",
                                    onClick = onScheduleNotificationClick,
                                    isLoading = notificationState.isScheduling
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog Edycji Słówka
    if (editState.showDialog && uiState.word != null) {
        AlertDialog(
            onDismissRequest = { onShowEditDialog(false) },
            title = {
                Text(text = "Edytuj słówko", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppTextField(
                        value = editState.word,
                        onValueChange = onEditWordChange,
                        label = "Słowo po angielsku"
                    )
                    AppTextField(
                        value = editState.translation,
                        onValueChange = onEditTranslationChange,
                        label = "Tłumaczenie po polsku"
                    )

                    if (editState.error != null) {
                        ErrorCard(error = editState.error)
                    }
                }
            },
            confirmButton = {
                LoadingButton(
                    text = "Zapisz",
                    onClick = onUpdateWordClick,
                    isLoading = editState.isUpdating
                )
            },
            dismissButton = {
                TextButton(onClick = { onShowEditDialog(false) }) {
                    Text("Anuluj")
                }
            }
        )
    }

    // Dialog Potwierdzenia Usunięcia Słówka
    if (deleteState.showConfirmation && uiState.word != null) {
        AlertDialog(
            onDismissRequest = { onShowDeleteConfirmation(false) },
            title = {
                Text(text = "Usunąć słówko?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Czy na pewno chcesz usunąć słówko '${uiState.word.word}'?\n\nRazem ze słówkiem usunięte zostaną dla niego wszystkie zaplanowane powiadomienia."
                )
            },
            confirmButton = {
                Button(
                    onClick = onDeleteWordClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !deleteState.isDeleting
                ) {
                    if (deleteState.isDeleting) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onError)
                    } else {
                        Text("Usuń")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { onShowDeleteConfirmation(false) }) {
                    Text("Anuluj")
                }
            }
        )
    }
}
