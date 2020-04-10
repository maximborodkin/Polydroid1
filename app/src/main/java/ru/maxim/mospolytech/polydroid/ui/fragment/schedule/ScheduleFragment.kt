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
import ru.maxim.mospolytech.polydroid.model.*
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
    val currentScheduleMode = ScheduleMode.CurrentWeek

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

        }
        scheduleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) { }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_schedule_top, menu)
        val searchMenu = menu.findItem(R.id.scheduleMenuSearch)
        val searchView: SearchView = searchMenu.actionView as SearchView
        val searchObjectsAdapter = SearchArrayAdapter(context!!, searchObjects)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return if (query.isNullOrEmpty()) false
                else {
                    val suitableObjects = searchObjects.filter { it.hasEntry(query) }
                    if (suitableObjects.isEmpty()) {
                        schedulePresenter.searchSchedule(query)
                    }
                    if (suitableObjects.size == 1) {
                        val searchObject = suitableObjects.first()
                        schedulePresenter.loadSchedule(searchObject.getType(), searchObject.id)
                    }
                    true
                }
            }

            override fun onQueryTextChange(newText: String?) = true
        })

        (searchView.findViewById(androidx.appcompat.R.id.search_src_text) as SearchView.SearchAutoComplete).apply {
            setTextColor(Color.WHITE)
            setAdapter(searchObjectsAdapter)
            imeOptions = EditorInfo.IME_NULL
            onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, itemIndex, _ ->
                val searchItem = adapterView.getItemAtPosition(itemIndex) as SearchObject
                setText(searchItem.name)
                schedulePresenter.loadSchedule(searchItem.getType(), searchItem.id)
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

    override fun onScheduleLoaded(type: ScheduleType, schedule: Schedule) {
        scheduleRefreshLayout.isRefreshing = false
        scheduleProgressBar.visibility = View.GONE
        drawSchedule(schedule.grid)
    }

    override fun onSearchObjectsLoaded(searchObjects: List<SearchObject>) {
        this.searchObjects.clear()
        this.searchObjects.addAll(searchObjects)
    }

    override fun onScheduleLoadError() {
        scheduleProgressBar.visibility = View.GONE
        scheduleRefreshLayout.isRefreshing = false
        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        if (!scheduleRefreshLayout.isRefreshing)
            scheduleProgressBar.visibility = View.VISIBLE
    }

    private fun drawSchedule(schedule: List<List<List<Lesson>>>) {
        scheduleListHost.removeAllViews()
        schedule.forEachIndexed { index, day ->
            val scheduleDayView = createScheduleDay(resources.getStringArray(R.array.days_of_week)[index], day)
            scheduleListHost.addView(scheduleDayView)
        }
    }

    private fun createScheduleDay(day: String, lessonsDay: List<List<Lesson>>) =
        (LayoutInflater.from(context).inflate(R.layout.item_schedule_day, scheduleListHost, false) as LinearLayout).apply {
            itemScheduleDayTitle.text = day
            if (lessonsDay.isNullOrEmpty()) itemScheduleDayMessage.text = getString(R.string.no_lessons_today)
            lessonsDay.forEach { lessonPosition ->
                lessonPosition.forEach { lesson ->
                    addView(createLessonItem(lesson, this))
                }
            }
    }

    private fun createLessonItem(lesson: Lesson, parent: ViewGroup) =
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
            val now = Date().time
            if (lesson.dateFrom > now || lesson.dateTo < now)
                itemScheduleDayBlur.visibility = View.VISIBLE

        }

    enum class ScheduleMode{
        CurrentWeek, FullSemester
    }
}
