package ru.maxim.mospolytech.polydroid.ui.fragment.schedule

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.maxim.mospolytech.polydroid.model.*
import ru.maxim.mospolytech.polydroid.model.SearchObject.SearchObjects
import ru.maxim.mospolytech.polydroid.repository.local.CacheManager
import ru.maxim.mospolytech.polydroid.repository.remote.service.ScheduleService
import ru.maxim.mospolytech.polydroid.repository.remote.service.SearchObjectsService
import java.util.*
import kotlin.collections.ArrayList

@InjectViewState
class SchedulePresenter : MvpPresenter<ScheduleView>() {

    private val scheduleService = ScheduleService()
    private val searchObjectsService = SearchObjectsService()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadSuggestions()
    }

    private fun loadSuggestions(){
        Log.i("SchedulePresenter", "loadSuggestions -> start loading...")
        val cachedSearchObjects = CacheManager.getCachedSearchObjects()
        if (cachedSearchObjects != null) {
            val (searchObjects, date) = cachedSearchObjects
            if (isActual(date)) {
                Log.i("SchedulePresenter", "loadSuggestions -> suggestions successfully loaded from cache.  SearchObjects: $searchObjects, date: $date ")
                viewState.onSearchObjectsLoaded(ArrayList<SearchObject>().apply {
                    addAll(searchObjects.groups)
                    addAll(searchObjects.teachers)
                    addAll(searchObjects.classrooms)
                })
                return
            }
        }
        searchObjectsService.getSearchObjects().enqueue(object : Callback<SearchObjects> {
            override fun onResponse(call: Call<SearchObjects>, response: Response<SearchObjects>) {
                Log.i("SchedulePresenter", "loadSuggestions -> onResponse: ${response.code()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.onSearchObjectsLoaded(ArrayList<SearchObject>().apply {
                            addAll(it.groups)
                            addAll(it.teachers)
                            addAll(it.classrooms)
                        })
                        CacheManager.saveSearchObjects(it)
                        Log.i("SchedulePresenter", "loadSuggestions -> suggestions successfully loaded from server.  SearchObjects: $it")
                    }
                }
            }
            override fun onFailure(call: Call<SearchObjects>, t: Throwable) {
                Log.i("SchedulePresenter", "loadSuggestions -> suggestions load error")
            }
        })
    }

    fun loadSchedule(type: ScheduleType, id: Int) {
        viewState.showLoading()
        Log.i("SchedulePresenter", "loadSchedule -> start loading Schedule{id: $id, type: ${type.name}}")
        val cachedSchedule = CacheManager.getSchedule(type, id)
        if (cachedSchedule != null){
            val (schedule, date) = cachedSchedule
            if (isActual(date)){
                viewState.onScheduleLoaded(type, schedule)
                Log.i("SchedulePresenter", "loadSchedule -> successfully loaded from cache")
                return
            }
        }
        when (type) {
            ScheduleType.Group -> scheduleService.getGroupSchedule(id)
            ScheduleType.Teacher -> scheduleService.getTeacherSchedule(id)
            ScheduleType.Classroom -> scheduleService.getClassroomSchedule(id)
        }.enqueue(object : Callback<Schedule> {
            override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                Log.i("SchedulePresenter", "loadSchedule -> successfully loaded from server")
                if (response.isSuccessful){
                    response.body()?.let {
                        viewState.onScheduleLoaded(type, it)
                        CacheManager.saveSchedule(type, it, id)
                    }
                } else{
                    viewState.onScheduleLoadError()
                }
            }

            override fun onFailure(call: Call<Schedule>, t: Throwable) {
                Log.i("SchedulePresenter", "loadSchedule -> schedule load error")
                viewState.onScheduleLoadError()
            }
        })
    }

    fun searchSchedule(query: String) {
        viewState.showLoading()
        Log.i("SchedulePresenter", "searchSchedule -> start loading with query '$query'")
        val cachedSchedule = CacheManager.getSchedule(query)
        if (cachedSchedule != null){
            val (schedule, date) = cachedSchedule
            if (isActual(date)){
                val type = when(schedule.type) {
                    "group" -> ScheduleType.Group
                    "teacher" -> ScheduleType.Teacher
                    "classroom" -> ScheduleType.Classroom
                    else -> null
                }
                if (type != null){
                    viewState.onScheduleLoaded(type, schedule)
                    return
                }
            }
        }
        scheduleService.searchSchedule(query).enqueue(object : Callback<Schedule> {
            override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                if (response.isSuccessful){
                    Log.i("SchedulePresenter", "searchSchedule -> schedule loaded")
                    response.body()?.let {
                        viewState.onScheduleLoaded(ScheduleType.Group, it)
                        CacheManager.saveSchedule(it, query)
                    }
                }else{
                    viewState.onScheduleLoadError()
                    if (response.code() == 300){
                        Log.i("SchedulePresenter", "searchSchedule -> schedule load multiple choices")
                        // Multiple choices
                    }
                }
            }

            override fun onFailure(call: Call<Schedule>, t: Throwable) {
                Log.i("SchedulePresenter", "searchSchedule -> schedule load error")
                viewState.onScheduleLoadError()
            }
        })
    }

    /**
     * Server updates schedule twice a day - at 19:00(7AM) and 00:00(12AM)
     * This method return true if [cacheDate] is before both synchronization points
     *
     * @property cacheDate date of cached schedule
     * @return is that schedule actual
     */
    private fun isActual(cacheDate: Date): Boolean {
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

        return !(cacheDate.before(todayMidnight) && now.after(todayMidnight)
                || cacheDate.before(todaySevenPM) && now.after(todaySevenPM))
    }
}
