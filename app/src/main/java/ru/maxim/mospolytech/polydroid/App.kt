package ru.maxim.mospolytech.polydroid

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import ru.maxim.mospolytech.polydroid.repository.local.CacheManager
import ru.maxim.mospolytech.polydroid.repository.local.PreferencesManager
import ru.maxim.mospolytech.polydroid.repository.remote.RetrofitClient
import ru.maxim.mospolytech.polydroid.service.AlarmReceiver
import ru.maxim.mospolytech.polydroid.util.DateFormatUtils
import ru.maxim.mospolytech.polydroid.util.PermissionManager


/**
 * Startup application class
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        RetrofitClient.context = applicationContext
        CacheManager.context = applicationContext
        PreferencesManager.context = applicationContext
        PermissionManager.context = applicationContext

        DateFormatUtils.currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            resources.configuration.locale
        }
        scheduleAlarm()
    }

    private fun scheduleAlarm() {
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        val pIntent = PendingIntent.getBroadcast(
            this, 100,
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val firstMillis = System.currentTimeMillis()
        val alarm = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, firstMillis,
            AlarmManager.INTERVAL_HALF_DAY, pIntent
        )
    }
}