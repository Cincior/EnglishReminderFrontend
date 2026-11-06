package pl.cincior.englishreminder

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import dagger.hilt.android.AndroidEntryPoint
import pl.cincior.englishreminder.auth.presentation.login.LoginRoute
import pl.cincior.englishreminder.navigation.AppNavigation
import pl.cincior.englishreminder.ui.theme.EnglishReminderTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnglishReminderTheme {
                EnglishReminderApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun EnglishReminderApp() {
    AppNavigation()
}