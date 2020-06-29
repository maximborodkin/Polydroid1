package ru.maxim.mospolytech.polydroid.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PatternMatcher
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.maxim.mospolytech.polydroid.R
import ru.maxim.mospolytech.polydroid.repository.local.PreferencesManager
import ru.maxim.mospolytech.polydroid.repository.remote.service.NotificationService
import ru.maxim.mospolytech.polydroid.ui.activity.notifications.NotificationsActivity


class NotificationsBackgroundService : Service(), CoroutineScope by MainScope()  {
    private val defaultNotificationChannelId = "DefaultNotificationChannel"

    private val notificationService = NotificationService()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        launch {
            if (notificationService.getNotifications(
                    PreferencesManager.lastNotificationId,
                    PreferencesManager.notificationsTarget?:""
                ).isNotEmpty()) {
                createNotificationChannel()
                createNotification("There are changes in the schedule")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification(text: String){
        val notificationsActivityIntent = Intent(this, NotificationsActivity::class.java)

        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(NotificationsActivity::class.java)
        stackBuilder.addNextIntent(notificationsActivityIntent)

        val notificationsActivityPendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, defaultNotificationChannelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Polydroid")
            .setContentText(text)
            .setContentIntent(notificationsActivityPendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val defaultChannel = NotificationChannel(
                defaultNotificationChannelId,
                getString(R.string.default_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            defaultChannel.description = getString(R.string.default_notification_channel_description)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(defaultChannel)
        }
    }
}