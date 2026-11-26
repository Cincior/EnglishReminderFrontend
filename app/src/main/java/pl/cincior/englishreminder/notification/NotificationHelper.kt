package pl.cincior.englishreminder.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import pl.cincior.englishreminder.MainActivity
import pl.cincior.englishreminder.R

object NotificationHelper {
    const val CHANNEL_ID = "english_reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "English reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Powiadomienia o nauce angielskiego"
            }

            val manager = context.getSystemService(
                NotificationManager::class.java
            )

            manager?.createNotificationChannel(channel)
            Log.d(
                "FCM",
                "Kanał: ${manager?.getNotificationChannel(CHANNEL_ID)?.name}"
            )
        }
    }

    fun showNotification(
        context: Context,
        title: String,
        body: String,
        wordId: String? = null
    ) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val deepLinkUri = if (!wordId.isNullOrBlank()) {
            "englishreminder://word_details/$wordId".toUri()
        } else {
            "englishreminder://word_list".toUri()
        }

        val intent = Intent(
            Intent.ACTION_VIEW,
            deepLinkUri,
            context,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }
}