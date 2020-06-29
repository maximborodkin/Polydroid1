package ru.maxim.mospolytech.polydroid.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val i = Intent(context, NotificationsBackgroundService::class.java)
        context.startService(i)
    }
}