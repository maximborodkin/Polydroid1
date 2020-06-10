package ru.maxim.mospolytech.polydroid.ui.activity.notifications

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.maxim.mospolytech.polydroid.repository.local.PreferencesManager
import ru.maxim.mospolytech.polydroid.repository.remote.service.NotificationService

@InjectViewState
class NotificationsPresenter : MvpPresenter<NotificationsView>(), CoroutineScope by MainScope() {
    private val notificationService = NotificationService()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadNotifications()
    }

    fun loadNotifications() {
        val lastIndex = PreferencesManager.lastNotificationId
        val target = PreferencesManager.notificationsTarget
        if (target.isNullOrEmpty()) return
        viewState.showLoading()
        launch {
            try {
                val notifications = notificationService.getNotifications(lastIndex, target)
                viewState.onNotificationsLoaded(notifications)
//                PreferencesManager.lastNotificationId = notifications.last().id
            } catch (e: Exception) {
                viewState.onNotificationsLoadError()
            }
        }
    }
}