package ru.maxim.mospolytech.polydroid.ui.fragment.schedule

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.maxim.mospolytech.polydroid.model.Schedule
import ru.maxim.mospolytech.polydroid.repository.local.CacheManager
import ru.maxim.mospolytech.polydroid.repository.local.PreferencesManager
import ru.maxim.mospolytech.polydroid.repository.remote.RetrofitClient
import ru.maxim.mospolytech.polydroid.repository.remote.service.ScheduleService
import ru.maxim.mospolytech.polydroid.repository.remote.service.SearchObjectsService
import ru.maxim.mospolytech.polydroid.util.PermissionManager
import java.util.*

@InjectViewState
class SchedulePresenter : MvpPresenter<ScheduleView>(), CoroutineScope by MainScope() {

    private val scheduleService = ScheduleService()
    private val searchObjectsService = SearchObjectsService()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadSuggestions()
        initView()
    }

    /**
     * View initializing method
     * Calls only on first view attach
     */
    private fun initView() {
        val lastRequest = PreferencesManager.lastRequest
        if (lastRequest.isNullOrEmpty()) {
            viewState.showStartScreen()
            return
        }
        val cachedSchedule = loadFromCache(lastRequest)
        if (cachedSchedule != null) {
            viewState.drawSchedule(cachedSchedule)
            if (!isActual(cachedSchedule.date)) {
                loadFromServer(lastRequest, cachedSchedule.date, false)
            }
        } else {
            loadFromServer(lastRequest, null, false)
        }
    }

    /**
     * Calls from view by executing SearchBar, pull to refresh or refresh button on notification bar
     * @param query is null in two last cases
     */
    fun loadSchedule(query: String?) {
        if (query == null) { // update
            val lastRequest = PreferencesManager.lastRequest
            if (lastRequest.isNullOrEmpty())
                viewState.showStartScreen()
            else
                loadFromServer(lastRequest, null, true)
        } else { // search
            val cachedSchedule = loadFromCache(query)
            if (cachedSchedule != null) {
                viewState.drawSchedule(cachedSchedule)
                if (!isActual(cachedSchedule.date)) {
                    loadFromServer(query, cachedSchedule.date, false)
                }
            } else {
                loadFromServer(query, null, false)
            }
        }
    }

    private fun loadSuggestions(){
        val cachedSearchObjects = CacheManager.getSearchObjects()
        if (cachedSearchObjects != null) {
            viewState.onSearchObjectsLoaded(cachedSearchObjects.toArrayList())
            if (isActual(cachedSearchObjects.date)) return
        }
        launch {
            try {
                val searchObjects = searchObjectsService.getSearchObjects()
                searchObjects.date = Date().time
                CacheManager.saveSearchObjects(searchObjects)
                viewState.onSearchObjectsLoaded(searchObjects.toArrayList())
            } catch (e: Exception) { }
        }
    }

    private fun loadFromCache(query: String): Schedule? {
        val cachedSchedule = CacheManager.getSchedule(query)
        if (cachedSchedule != null) PreferencesManager.lastRequest = query
        return cachedSchedule
    }

    private fun loadFromServer(query: String, cachedScheduleTime: Long?, isUpdate: Boolean) {
        val hasANSPermission = PermissionManager.hasANSPermission()
        val hasInternetPermission = PermissionManager.hasInternetPermission()
        val isOnline = if (hasANSPermission) RetrofitClient.isOnline() else true

        if (!hasInternetPermission){
            viewState.showPermissionNotification()
            return
        }

        if (isOnline){
            if (cachedScheduleTime == null && !isUpdate)
                viewState.showLoading()
            else
                viewState.showLoadingNotification()

            launch {
                try {
                    val schedule = scheduleService.getSchedule(query)
                    viewState.drawSchedule(schedule)
                    PreferencesManager.lastRequest = query
                    CacheManager.saveSchedule(schedule, query)
                } catch (e: Exception) {
                    if (cachedScheduleTime == null)
                        viewState.showNoConnectionNotification()
                    else
                        viewState.showTimeNotification(cachedScheduleTime)
                }
            }
        } else {
            if (cachedScheduleTime == null)
                viewState.showNoConnectionNotification()
            else
                viewState.showTimeNotification(cachedScheduleTime)
        }
    }

    /**
     * Server updates schedule twice a day - at 19:00(7AM) and 00:00(12AM)
     * This method return true if [date] is before both synchronization points
     *
     * @property date date of cached schedule
     * @return is that schedule actual
     */
    private fun isActual(date: Long): Boolean {
        val todayMidnight = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val todaySevenPM = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val now = Date()
        val cacheDate = Date(date)

        return !(cacheDate.before(todayMidnight) && now.after(todayMidnight)
                || cacheDate.before(todaySevenPM) && now.after(todaySevenPM))
    }
}