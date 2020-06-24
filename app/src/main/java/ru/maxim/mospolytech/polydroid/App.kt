package ru.maxim.mospolytech.polydroid

import android.app.*
import android.os.Build
import ru.maxim.mospolytech.polydroid.repository.local.CacheManager
import ru.maxim.mospolytech.polydroid.repository.local.PreferencesManager
import ru.maxim.mospolytech.polydroid.repository.remote.RetrofitClient
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
            resources.configuration.locale
        }
    }
}