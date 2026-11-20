package pl.cincior.englishreminder.feature.auth.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pl.cincior.englishreminder.core.presentation.components.AppTextField
import pl.cincior.englishreminder.core.presentation.components.ErrorCard
import pl.cincior.englishreminder.core.presentation.components.LoadingButton

@Composable
fun RegisterRoute(
    onBackClicked: () -> Unit,
    onRegisterSuccess: (String) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) {
            onRegisterSuccess(uiState.email)
        }
    }

    RegisterScreen(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onNameChange = viewModel::onNameChanged,
        onSurnameChange = viewModel::onSurnameChanged,
        onNicknameChange = viewModel::onNicknameChanged,
        onEmailChange = viewModel::onEmailChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onRegisterClick = viewModel::registerUser
    )
}

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onBackClicked: () -> Unit,
    onNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    onNicknameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Stwórz konto",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Wypełnij formularz, aby dołączyć do English Reminder",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = "Imię",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(10.dp))

            AppTextField(
                value = uiState.surname,
                onValueChange = onSurnameChange,
                label = "Nazwisko",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(10.dp))

            AppTextField(
                value = uiState.nickname,
                onValueChange = onNicknameChange,
                label = "Pseudonim",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(10.dp))

            AppTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = "Adres email",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            AppTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = "Hasło",
                isPassword = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                ErrorCard(error = uiState.error)
            }

            Spacer(modifier = Modifier.height(20.dp))

            LoadingButton(
                text = "Zarejestruj się",
                onClick = onRegisterClick,
                isLoading = uiState.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBackClicked,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Mam już konto (Wróć)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
