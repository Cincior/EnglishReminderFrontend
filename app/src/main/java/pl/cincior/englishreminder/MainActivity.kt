package pl.cincior.englishreminder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import pl.cincior.englishreminder.feature.auth.presentation.login.LoginRoute
import pl.cincior.englishreminder.navigation.AppNavigation
import pl.cincior.englishreminder.notification.NotificationHelper
import pl.cincior.englishreminder.ui.theme.EnglishReminderTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        askNotificationPermission(this)
        NotificationHelper.createNotificationChannel(this)
        setContent {
            EnglishReminderTheme {
                EnglishReminderApp()
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Log.d("FCM permissions", "Zgoda na powiadomienia przyznana")
            } else {
                Log.d("FCM permissions", "Zgoda na powiadomienia odrzucona")
            }
        }

    private fun askNotificationPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun EnglishReminderApp() {
    AppNavigation()
}