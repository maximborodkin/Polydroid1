package ru.maxim.mospolytech.polydroid.ui.fragment.schedule

import com.arellomobile.mvp.MvpView
import ru.maxim.mospolytech.polydroid.model.Schedule
import ru.maxim.mospolytech.polydroid.model.SearchObject

interface ScheduleView : MvpView {

    fun showStartScreen()
    fun onSearchObjectsLoaded(searchObjectsList: List<SearchObject>)
    fun drawSchedule(schedule: Schedule)
    fun showLoadingNotification()
    fun showNoConnectionNotification()
    fun showNetworkErrorNotification()
    fun showPermissionNotification()
    fun showTimeNotification(time: Long)
    fun showLoading()
}
