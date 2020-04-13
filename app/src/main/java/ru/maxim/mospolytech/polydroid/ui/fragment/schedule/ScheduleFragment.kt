package ru.maxim.mospolytech.polydroid.ui.fragment.schedule

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.preference.PreferenceManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.item_lesson.view.*
import kotlinx.android.synthetic.main.item_schedule_day.view.*
import ru.maxim.mospolytech.polydroid.R
import ru.maxim.mospolytech.polydroid.model.Lesson
import ru.maxim.mospolytech.polydroid.model.Schedule
import ru.maxim.mospolytech.polydroid.model.SearchObject
import ru.maxim.mospolytech.polydroid.repository.local.PreferencesManager
import ru.maxim.mospolytech.polydroid.ui.activity.notifications.NotificationsActivity
import ru.maxim.mospolytech.polydroid.ui.activity.settings.SettingsActivity
import ru.maxim.mospolytech.polydroid.util.DateFormatUtils
import java.util.*
import kotlin.collections.ArrayList

class ScheduleFragment : MvpAppCompatFragment(), ScheduleView {

    @InjectPresenter
    lateinit var schedulePresenter: SchedulePresenter

    private lateinit var preferenceManager: SharedPreferences
    private val searchObjects = ArrayList<SearchObject>()
    private var schedule: Schedule? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_schedule, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scheduleRefreshLayout.setOnRefreshListener {
            schedulePresenter.loadLastSchedule()
        }
        scheduleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) { schedulePresenter.loadSchedule() }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_schedule_top, menu)
        val searchMenu = menu.findItem(R.id.scheduleMenuSearch)
        val searchView: SearchView = searchMenu.actionView as SearchView
        val searchBarAdapter = SearchArrayAdapter(context!!, searchObjects)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.isIconified = true
                searchMenu.collapseActionView()
                return if (query.isNullOrEmpty()) false
                else {
                    val suitableObjects = searchObjects.filter { it.hasEntry(query) }
                    if (suitableObjects.isEmpty()) {
                        schedulePresenter.loadSchedule("schedule/search?q=$query")
                    }
                    if (suitableObjects.size == 1) {
                        val searchObject = suitableObjects.first()
                        schedulePresenter.loadSchedule("schedule/${searchObject.getType()}/${searchObject.id}")
                    }
                    true
                }
            }

            override fun onQueryTextChange(newText: String?) = true
        })
        PreferencesManager.lastRequest?.split("/")?.last()?.let { searchView.setQuery(it, false) }
        (searchView.findViewById(androidx.appcompat.R.id.search_src_text) as SearchView.SearchAutoComplete).apply {
            setTextColor(Color.WHITE)
            setAdapter(searchBarAdapter)
            imeOptions = EditorInfo.IME_NULL
            onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, itemIndex, _ ->
                val searchItem = adapterView.getItemAtPosition(itemIndex) as SearchObject
                setText(searchItem.name)
                schedulePresenter.loadSchedule("schedule/${searchItem.getType()}/${searchItem.id}")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.scheduleMenuNotifications -> { startActivity(Intent(context, NotificationsActivity::class.java)); true }
            R.id.scheduleMenuSettings -> { startActivity(Intent(context, SettingsActivity::class.java)); true }
            else -> false
        }
    }

    override fun onScheduleLoaded(schedule: Schedule) {
        scheduleRefreshLayout.isRefreshing = false
        scheduleProgressBar.visibility = View.GONE
        this.schedule = schedule
        drawSchedule()
    }

    override fun onSearchObjectsLoaded(searchObjects: List<SearchObject>) {
        this.searchObjects.clear()
        this.searchObjects.addAll(searchObjects)
    }

    override fun onScheduleLoadError() {
        scheduleProgressBar.visibility = View.GONE
        scheduleRefreshLayout.isRefreshing = false
        schedule?.date?.let {
            Toast.makeText(context, DateFormatUtils.simplifyDate(it), Toast.LENGTH_SHORT).show()
        }
    }

    override fun showLoading() {
        if (!scheduleRefreshLayout.isRefreshing)
            scheduleProgressBar.visibility = View.VISIBLE
    }

    private fun drawSchedule() {
        if (schedule?.grid != null) {
            scheduleListHost.removeAllViews()
            schedule?.grid?.forEachIndexed { index, day ->
                val scheduleDayView = createScheduleDay(resources.getStringArray(R.array.days_of_week)[index], day)
                scheduleListHost.addView(scheduleDayView)
            }
        }
    }

    private fun createScheduleDay(day: String, lessonsDay: List<List<Lesson>>) =
        (LayoutInflater.from(context).inflate(R.layout.item_schedule_day, scheduleListHost, false) as LinearLayout).apply {
            itemScheduleDayTitle.text = day
            if (lessonsDay.isNullOrEmpty()) itemScheduleDayMessage.text = getString(R.string.no_lessons_today)
            val now = Date().time
            lessonsDay.forEach { lessonPosition ->
                lessonPosition.forEach { lesson ->
                    val inRange = lesson.dateFrom < now && lesson.dateTo+1000*60*60*24 > now
                    if (scheduleTabLayout.selectedTabPosition == 1
                        || scheduleTabLayout.selectedTabPosition == 0 && inRange)
                    addView(createLessonItem(lesson, this, !inRange))
                }
            }
    }

    private fun createLessonItem(lesson: Lesson, parent: ViewGroup, hasBlur: Boolean) =
        LayoutInflater.from(context).inflate(R.layout.item_lesson, parent, false).apply {
            val classroomsString = SpannableStringBuilder().apply {
                lesson.classrooms.forEach { classroom ->
                    append(classroom.name).append(" ")
                    val colorSpan = try {
                        ForegroundColorSpan(Color.parseColor(classroom.color))
                    } catch (e: IllegalArgumentException) {
                        ForegroundColorSpan(Color.BLACK)
                    }
                    setSpan(colorSpan, length - classroom.name.length - 1, length, SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
            if (classroomsString.isEmpty()) itemLessonClassrooms.visibility = View.GONE
            else itemLessonClassrooms.apply {
                text = classroomsString
                movementMethod = LinkMovementMethod.getInstance()
            }

            itemLessonTitle.text = lesson.name

            StringBuilder().apply {
                lesson.teachers.forEachIndexed { index, teacher ->
                    append("${teacher.name} ${if (index < lesson.teachers.size - 1) "\n" else ""}")
                }
                if (isEmpty()) itemLessonTeachers.visibility = View.GONE
                else itemLessonTeachers.text = toString()
            }

            val dateFrom = DateFormatUtils.simplifyDate(lesson.dateFrom)
            val dateTo = DateFormatUtils.simplifyDate(lesson.dateTo)
            itemLessonDates.text = if (dateFrom == dateTo) dateFrom else "$dateFrom - $dateTo"
            if (hasBlur) itemScheduleDayBlur.visibility = View.VISIBLE
        }
}
