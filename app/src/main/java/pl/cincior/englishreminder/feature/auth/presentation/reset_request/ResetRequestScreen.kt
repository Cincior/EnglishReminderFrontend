package pl.cincior.englishreminder.feature.auth.presentation.reset_request

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
fun ResetRequestRoute(
    onBackClicked: () -> Unit,
    onRequestSuccess: (email: String) -> Unit,
    viewModel: ResetRequestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isRequested) {
        if (uiState.isRequested) {
            onRequestSuccess(uiState.email)
        }
    }

    ResetRequestScreen(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onEmailChange = viewModel::onEmailChanged,
        onSendClick = viewModel::sendResetRequest
    )
}

@Composable
fun ResetRequestScreen(
    uiState: ResetRequestUiState,
    onBackClicked: () -> Unit,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit
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
                text = "Resetowanie hasła",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Wpisz swój adres e-mail, aby otrzymać kod do zresetowania hasła",
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
                    imeAction = ImeAction.Done
                )
            )

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                ErrorCard(error = uiState.error)
            }

            Spacer(modifier = Modifier.height(20.dp))

            LoadingButton(
                text = "Wyślij kod resetujący",
                onClick = onSendClick,
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
