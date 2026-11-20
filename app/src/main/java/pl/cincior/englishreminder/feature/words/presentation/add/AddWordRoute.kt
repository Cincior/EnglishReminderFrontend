package pl.cincior.englishreminder.feature.words.presentation.add

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pl.cincior.englishreminder.core.presentation.components.AppTextField
import pl.cincior.englishreminder.core.presentation.components.ErrorCard
import pl.cincior.englishreminder.core.presentation.components.LoadingButton
import pl.cincior.englishreminder.feature.words.domain.model.WordCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordRoute(
    onNavigateBack: () -> Unit,
    viewModel: AddWordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
    val timeState = rememberTimePickerState()

    LaunchedEffect(uiState.isWordAdded) {
        if (uiState.isWordAdded && uiState.isNotificationCreated) {
            onNavigateBack()
        }
    }

    LaunchedEffect(dateState.selectedDateMillis) {
        viewModel.onScheduleDateChanged(dateState.selectedDateMillis)
    }

    LaunchedEffect(timeState.hour, timeState.minute) {
        viewModel.onScheduleTimeChanged(timeState.hour, timeState.minute)
    }


    AddWordScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onWordChange = viewModel::onWordChanged,
        onTranslationChange = viewModel::onTranslationChanged,
        onForceDuplicateChange = viewModel::onForceDuplicateChanged,
        onCategorySelect = viewModel::onCategorySelected,
        onScheduleNotificationChange = viewModel::onScheduleNotificationChanged,
        onAddWordClick = viewModel::addWord,
        dateState = dateState,
        timeState = timeState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordScreen(
    uiState: AddWordUiState,
    onNavigateBack: () -> Unit,
    onWordChange: (String) -> Unit,
    onTranslationChange: (String) -> Unit,
    onForceDuplicateChange: (Boolean) -> Unit,
    onCategorySelect: (WordCategory) -> Unit,
    onScheduleNotificationChange: (Boolean) -> Unit,
    onAddWordClick: () -> Unit,
    dateState: DatePickerState,
    timeState: TimePickerState
) {
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dodaj nowe słówko",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppTextField(
                value = uiState.word,
                onValueChange = onWordChange,
                label = "Słowo po angielsku (np. Apple)",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            AppTextField(
                value = uiState.translation,
                onValueChange = onTranslationChange,
                label = "Tłumaczenie (np. Jabłko)",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            // Wybór kategorii
            Column {
                Text(
                    text = "Kategoria słówka",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Wybierz kategorię") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { categoryDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
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

            // Przełącznik: Wymuś duplikat
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Wymuś dodanie duplikatu",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Pozwala dodać słowo, nawet jeśli już istnieje w bazie",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                    Switch(
                        checked = uiState.forceDuplicate,
                        onCheckedChange = onForceDuplicateChange
                    )
                }
            }

            // Przełącznik: Zaplanuj powiadomienie
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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
                                text = "Zaplanuj powiadomienie",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Wyśle powiadomienie o powtórce w wybranym czasie",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        Switch(
                            checked = uiState.scheduleNotification,
                            onCheckedChange = onScheduleNotificationChange
                        )
                    }

                    if (uiState.scheduleNotification) {

                        TimeInput(
                            state = timeState,
                        )
                        DatePicker(state = dateState)
//                        AppTextField(
//                            value = uiState.scheduleTime,
//                            onValueChange = onScheduleTimeChange,
//                            label = "Godzina/Czas powiadomienia (np. 10:00)",
//                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
//                        )
                    }
                }
            }

            if (uiState.error != null) {
                ErrorCard(error = uiState.error)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LoadingButton(
                text = "Dodaj słówko",
                onClick = onAddWordClick,
                isLoading = uiState.isLoading
            )
        }
    }
}
