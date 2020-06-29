package ru.maxim.mospolytech.polydroid.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import ru.maxim.mospolytech.polydroid.util.PermissionManager.context

/**
 *  Singleton object for managing permissions
 *  @property context sets from [ru.maxim.mospolytech.polydroid.App] class
 */
object PermissionManager {

    lateinit var context: Context

    fun hasANSPermission() =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED

    fun hasInternetPermission() =
        ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED

    fun hasRBCPermission() =
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_BOOT_COMPLETED) == PackageManager.PERMISSION_GRANTED

}