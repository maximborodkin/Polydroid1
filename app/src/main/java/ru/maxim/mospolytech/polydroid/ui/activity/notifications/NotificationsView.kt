package ru.maxim.mospolytech.polydroid.ui.activity.notifications

import com.arellomobile.mvp.MvpView
import ru.maxim.mospolytech.polydroid.model.Notification

interface NotificationsView : MvpView {

    fun onNotificationsLoaded(notifications: List<Notification>)

    fun onNotificationsLoadError()

    fun showLoading()
}