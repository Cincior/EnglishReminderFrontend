package pl.cincior.englishreminder.feature.auth.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                style = (MaterialTheme.typography.headlineLarge),
                text = "English Reminder",
                textAlign = TextAlign.Center
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = "Logowanie",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = { Text("Email") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = { Text("Hasło") },
                visualTransformation = PasswordVisualTransformation()
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error.asString(),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onLoginClick,
                enabled = !uiState.isLoading
            ) {
                Text(if (uiState.isLoading) "Logowanie..." else "Zaloguj")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onForgotPasswordClick,
                enabled = !uiState.isLoading
            ) {
                Text(
                    text = "Nie pamiętasz hasła?",
                    textDecoration = TextDecoration.Underline
                )
            }

            TextButton(
                onClick = onRegisterClick,
                enabled = !uiState.isLoading
            ) {
                Text(
                    text = "Nie masz konta? Zarejestruj się",
                    textDecoration = TextDecoration.Underline
                )
            }

            TextButton(
                onClick = onVerifyAccountClick,
                enabled = !uiState.isLoading
            ) {
                Text(
                    text = "Nie zweryfikowałeś konta? Zrób to teraz",
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}