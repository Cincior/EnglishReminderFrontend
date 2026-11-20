package pl.cincior.englishreminder.feature.auth.presentation.verify

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
fun VerifyAccountRoute(
    onBackClicked: () -> Unit,
    onVerifySuccess: () -> Unit,
    viewModel: VerifyAccountViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isVerified) {
        if (uiState.isVerified) {
            onVerifySuccess()
        }
    }

    VerifyAccountScreen(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onEmailChange = viewModel::onEmailChanged,
        onCodeChange = viewModel::onCodeChanged,
        onVerifyClick = viewModel::verifyAccount
    )
}

@Composable
fun VerifyAccountScreen(
    uiState: VerifyAccountUiState,
    onBackClicked: () -> Unit,
    onEmailChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit
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
                text = "Weryfikacja konta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Wpisz adres e-mail oraz kod weryfikacyjny z wiadomości e-mail",
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
                value = uiState.code,
                onValueChange = onCodeChange,
                label = "Kod weryfikacyjny",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                ErrorCard(error = uiState.error)
            }

            Spacer(modifier = Modifier.height(20.dp))

            LoadingButton(
                text = "Zweryfikuj konto",
                onClick = onVerifyClick,
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
                    text = "Wróć do logowania",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
