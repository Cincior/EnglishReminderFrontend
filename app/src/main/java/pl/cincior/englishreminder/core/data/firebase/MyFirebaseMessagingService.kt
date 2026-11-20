package pl.cincior.englishreminder.core.data.firebase

import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pl.cincior.englishreminder.notification.NotificationHelper

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onRegistered(installationId: String) {
        super.onRegistered(installationId)
        Log.d("FCM", "Zarejestrowano nowe Installation ID: $installationId")

        sendRegistrationToServer(installationId)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM", "================================")
        Log.d("FCM", "OTRZYMANO WIADOMOŚĆ!")
        Log.d("FCM", "Message ID: ${message.messageId}")
        Log.d("FCM", "Title: ${message.notification?.title}")
        Log.d("FCM", "Body: ${message.notification?.body}")
        Log.d("FCM", "================================")

        NotificationHelper.showNotification(
            applicationContext,
            message.notification?.title ?: "Blad tytulu",
            message.notification?.body ?: "Blad tresci"
        )
    }

    private fun sendRegistrationToServer(installationId: String) {
        val workRequest = OneTimeWorkRequestBuilder<FcmRegistrationWorker>()
            .setInputData(
                workDataOf(FcmRegistrationWorker.TOKEN_KEY to installationId)
            )
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            FcmRegistrationWorker.UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}