package ru.maxim.mospolytech.polydroid.util

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

object DateFormatUtils : DateUtils() {

    var currentLocale: Locale = Locale("ru","RU")

    fun simplifyDate(date: Long) =
        SimpleDateFormat("dd MMM", currentLocale).format(Date(date)).replace(".", "")

    fun getCurrentDay(): String = SimpleDateFormat("dd", currentLocale).format(Date())

    fun getCurrentMonth(): String = SimpleDateFormat("MMM", currentLocale).format(Date())
}