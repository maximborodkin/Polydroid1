package ru.maxim.mospolytech.polydroid.ui.fragment.schedule

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.maxim.mospolytech.polydroid.model.Schedule
import ru.maxim.mospolytech.polydroid.repository.local.CacheManager
import ru.maxim.mospolytech.polydroid.repository.local.PreferencesManager
import ru.maxim.mospolytech.polydroid.repository.remote.service.ScheduleService
import ru.maxim.mospolytech.polydroid.repository.remote.service.SearchObjectsService
import java.util.*

@InjectViewState
class SchedulePresenter : MvpPresenter<ScheduleView>(), CoroutineScope by MainScope() {

    private val scheduleService = ScheduleService()
    private val searchObjectsService = SearchObjectsService()
    private lateinit var schedule: Schedule

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadSuggestions()
        loadLastSchedule()
    }

    fun loadLastSchedule(){
        val lastRequest = PreferencesManager.lastRequest
        if (!lastRequest.isNullOrEmpty()){
            loadSchedule(lastRequest)
        }
    }

    private fun loadSuggestions(){
        val cachedSearchObjects = CacheManager.getCachedSearchObjects()
        if (cachedSearchObjects != null) {
            viewState.onSearchObjectsLoaded(cachedSearchObjects.toArrayList())
            if (isActual(cachedSearchObjects.date))return
        }
        launch {
            try {
                val searchObjects = searchObjectsService.getSearchObjects()
                viewState.onSearchObjectsLoaded(searchObjects.toArrayList())
            } catch (e: Exception) { }
        }
    }

    fun loadSchedule(query: String) {
        viewState.showLoading()

        val cachedSchedule = CacheManager.getSchedule(query)
        if (cachedSchedule != null){
            schedule = cachedSchedule
            viewState.onScheduleLoaded(schedule)
            PreferencesManager.lastRequest = query
            if (isActual(cachedSchedule.date)) {
                return
            }
        }
        launch {
            try {
                schedule = scheduleService.getSchedule(query)
                viewState.onScheduleLoaded(schedule)
                CacheManager.saveSchedule(schedule, query)
            }catch (e: Exception) {
                viewState.onScheduleLoadError()
            }
        }
    }

    fun loadSchedule() {
        if (::schedule.isInitialized)
            viewState.onScheduleLoaded(schedule)
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