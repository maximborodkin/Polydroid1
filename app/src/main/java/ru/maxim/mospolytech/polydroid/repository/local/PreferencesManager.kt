package ru.maxim.mospolytech.polydroid.repository.local

import android.content.Context
import androidx.preference.PreferenceManager

/**
 * Singleton object for access to SharedPreferences
 *  @property context sets from [ru.maxim.mospolytech.polydroid.App] class
 */
object PreferencesManager {

    lateinit var context: Context
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    /**
     * SharedPreferences keys
     */
    private const val isInitializedKey = "is_initialized"
    private const val isEnabledNotificationsKey = "enable_notifications"
    const val notificationsTargetKey = "notifications_target"
    const val notificationsStyleKey = "target_type"
    private const val isDarkThemeKey = "dark_theme"
    private const val lastNotificationIdKey = "last_notification_id"
    private const val lastRequestKey = "last_request"


    var isInitialized: Boolean
        get() = sharedPreferences.getBoolean(isInitializedKey, false)
        set(value) { sharedPreferences.edit().putBoolean(isInitializedKey, value).apply() }

    var isNotificationsEnabled: Boolean
        get() = sharedPreferences.getBoolean(isEnabledNotificationsKey, false)
        set(value) { sharedPreferences.edit().putBoolean(isEnabledNotificationsKey, value).apply() }

    var notificationsTarget: String?
        get() = sharedPreferences.getString(notificationsTargetKey, null)
        set(value) { sharedPreferences.edit().putString(notificationsTargetKey, value).apply() }

    var notificationsStyle: String
        get() = sharedPreferences.getString(notificationsStyleKey, null)?:"student"
        set(value) { sharedPreferences.edit().putString(notificationsTargetKey, value).apply() }

    var isDarkTheme: Boolean
        get() = sharedPreferences.getBoolean(isDarkThemeKey, false)
        set(value) { sharedPreferences.edit().putBoolean(isDarkThemeKey, value).apply() }

    var lastNotificationId: Int
        get() = sharedPreferences.getInt(lastNotificationIdKey, 0)
        set(value) { sharedPreferences.edit().putInt(lastNotificationIdKey, value).apply() }

    var lastRequest: String?
        get() = sharedPreferences.getString(lastRequestKey, null)
        set(value) { sharedPreferences.edit().putString(lastRequestKey, value).apply() }
}