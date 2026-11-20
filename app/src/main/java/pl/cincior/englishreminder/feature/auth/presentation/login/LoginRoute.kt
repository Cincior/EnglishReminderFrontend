package pl.cincior.englishreminder.feature.auth.presentation.login

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun LoginRoute(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onVerifyAccountClick: (String) -> Unit,
    onForgotPasswordClick: (email: String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    LoginScreen(
        uiState = uiState,
        onRegisterClick = onRegisterClick,
        onVerifyAccountClick = { onVerifyAccountClick(uiState.email) },
        onForgotPasswordClick = { onForgotPasswordClick(uiState.email) },
        onEmailChange = viewModel::onEmailChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onLoginClick = viewModel::onLoginClick
    )
}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onRegisterClick: () -> Unit,
    onVerifyAccountClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
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
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "English Reminder",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Text(
                text = "Zaloguj się",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Wpisz swoje dane, aby kontynuować naukę",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = "Adres email",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onForgotPasswordClick,
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text = "Nie pamiętasz hasła?",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorCard(error = uiState.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LoadingButton(
                text = "Zaloguj się",
                onClick = onLoginClick,
                isLoading = uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onRegisterClick,
                enabled = !uiState.isLoading
            ) {
                Text(
                    text = "Nie masz konta? Zarejestruj się",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            TextButton(
                onClick = onVerifyAccountClick,
                enabled = !uiState.isLoading
            ) {
                Text(
                    text = "Masz kod weryfikacyjny? Zweryfikuj konto",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
