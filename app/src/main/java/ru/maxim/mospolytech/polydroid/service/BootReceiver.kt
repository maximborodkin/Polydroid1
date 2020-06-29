package ru.maxim.mospolytech.polydroid.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.maxim.mospolytech.polydroid.ui.activity.main.MainActivity
import ru.maxim.mospolytech.polydroid.util.PermissionManager


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.startActivity(Intent(context, MainActivity::class.java))
        if (!PermissionManager.hasRBCPermission()) return
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            context.startService(Intent(context, NotificationsBackgroundService::class.java))
        }
    }
}