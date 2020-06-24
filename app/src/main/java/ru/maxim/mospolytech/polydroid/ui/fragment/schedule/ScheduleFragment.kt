package ru.maxim.mospolytech.polydroid.ui.fragment.schedule

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.item_lesson.view.*
import kotlinx.android.synthetic.main.item_schedule_day.view.*
import ru.maxim.mospolytech.polydroid.R
import ru.maxim.mospolytech.polydroid.model.Lesson
import ru.maxim.mospolytech.polydroid.model.Schedule
import ru.maxim.mospolytech.polydroid.model.ScheduleType
import ru.maxim.mospolytech.polydroid.model.SearchObject
import ru.maxim.mospolytech.polydroid.ui.activity.notifications.NotificationsActivity
import ru.maxim.mospolytech.polydroid.ui.activity.settings.SettingsActivity
import ru.maxim.mospolytech.polydroid.util.DateFormatUtils
import java.util.*
import kotlin.collections.ArrayList

class ScheduleFragment : MvpAppCompatFragment(), ScheduleView,
    ScheduleDialogFragment.OnItemClickListener {

    @InjectPresenter
    lateinit var schedulePresenter: SchedulePresenter
    private val searchObjects = ArrayList<SearchObject>()
    private val currentDayIndex: Int = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2
    private lateinit var currentScheduleType: ScheduleType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_schedule, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scheduleRefreshLayout.setOnRefreshListener {
            schedulePresenter.loadSchedule(null)
        }

        scheduleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) { schedulePresenter.loadSchedule(null) }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })

        scheduleStartMessage.text = SpannableStringBuilder().apply {
            val message = getString(R.string.schedule_start_message)
            append(message)
            setSpan(
                ImageSpan(requireContext(), R.drawable.ic_search_grey_24dp),
                message.indexOf("%icon%") + "%icon%".length,
                message.indexOf("%icon%") + "%icon%".length + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        scheduleDateBtn.setOnClickListener {
            val scheduleDayView = view.findViewById<LinearLayout>(0x999999+currentDayIndex)
            if (scheduleDayView != null){
                scheduleScrollView.smoothScrollTo(0, scheduleDayView.top)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_schedule_top, menu)
        val searchMenu = menu.findItem(R.id.scheduleMenuSearch)
        val searchView: SearchView = searchMenu.actionView as SearchView
        val searchBarAdapter = SearchArrayAdapter(requireContext(), searchObjects)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchMenu.collapseActionView()
                return if (query.isNullOrEmpty()) false
                else {
                    val suitableObjects = searchObjects.filter { it.hasEntry(query) }
                    if (suitableObjects.isEmpty()) {
                        schedulePresenter.loadSchedule("schedule/search?q=$query")
                    }
                    if (suitableObjects.size == 1) {
                        val searchObject = suitableObjects.first()
                        schedulePresenter
                            .loadSchedule("schedule/${searchObject.getType()}/${searchObject.id}")
                    }
                    true
                }
            }

            override fun onQueryTextChange(newText: String?) = true
        })

        (searchView.findViewById(androidx.appcompat.R.id.search_src_text)
                as SearchView.SearchAutoComplete).apply {
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
            R.id.scheduleMenuNotifications -> {
                startActivity(Intent(context, NotificationsActivity::class.java))
                true
            }
            R.id.scheduleMenuSettings -> {
                startActivity(Intent(context, SettingsActivity::class.java))
                true
            }
            else -> false
        }
    }

    override fun showStartScreen() {
        scheduleRefreshLayout.isRefreshing = false
        scheduleProgressBar.visibility = View.GONE
        scheduleNotificationLayout.visibility = View.GONE
        scheduleStartMessage.visibility = View.VISIBLE
    }

    override fun onSearchObjectsLoaded(searchObjectsList: List<SearchObject>) {
        searchObjects.clear()
        searchObjects.addAll(searchObjectsList)
        activity?.invalidateOptionsMenu()
    }

    override fun showLoadingNotification() {
        scheduleRefreshLayout.isRefreshing = false
        scheduleNotificationLayout.visibility = View.VISIBLE
        scheduleProgressBar.visibility = View.GONE
        scheduleNotificationTitle.text = getString(R.string.loading_schedule)
    }

    override fun showNoConnectionNotification() {
        scheduleRefreshLayout.isRefreshing = false
        scheduleProgressBar.visibility = View.GONE
        scheduleNotificationTitle.text = getString(R.string.no_internet_connection)
        scheduleNotificationLayout.visibility = View.VISIBLE
    }

    override fun showNetworkErrorNotification() {
        scheduleRefreshLayout.isRefreshing = false
        scheduleNotificationLayout.visibility = View.VISIBLE
        scheduleProgressBar.visibility = View.GONE
        scheduleNotificationTitle.text = getString(R.string.loading_error)
    }

    override fun showPermissionNotification() {
        Snackbar
            .make(scheduleRefreshLayout, R.string.permission_notification, Snackbar.LENGTH_LONG)
            .setAction(R.string.close) {
                Toast.makeText(context, "close", Toast.LENGTH_SHORT).show()
            }
            .setAction(R.string.settings) {
                Toast.makeText(context, "settings", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    override fun showTimeNotification(time: Long) {
        scheduleRefreshLayout.isRefreshing = false
        scheduleProgressBar.visibility = View.GONE
        scheduleNotificationLayout.visibility = View.VISIBLE
        scheduleNotificationTitle.text =
            getString(R.string.cached_schedule, DateFormatUtils.simplifyDate(time))
    }

    override fun showLoading() {
        scheduleStartMessage.visibility = View.GONE
        scheduleProgressBar.visibility = View.VISIBLE
    }

    override fun drawSchedule(schedule: Schedule) {
        scheduleRefreshLayout.isRefreshing = false
        scheduleProgressBar.visibility = View.GONE
        scheduleNotificationLayout.visibility = View.GONE
        scheduleListHost.removeAllViews()
        activity?.title = schedule.title
        currentScheduleType = when(schedule.type) {
            "teacher" -> ScheduleType.Teacher
            "classroom" -> ScheduleType.Classroom
            else -> ScheduleType.Group
        }

        schedule.grid.forEachIndexed { index, day ->
            var lessonsCount = 0
            day.forEach { lessonsCount += it.size }
            if (!(index == 6 && lessonsCount == 0))
                scheduleListHost.addView(createScheduleDay(index, day))
        }
    }

    /**
     * Inflates and fills schedule day view [R.layout.item_schedule_day]
     * @param dayIndex is day of week name
     * @param lessonsDay list of [Lesson] objects grouped by time
     * @return [ViewGroup] [R.layout.item_schedule_day]
     */
    private fun createScheduleDay(dayIndex: Int, lessonsDay: List<List<Lesson>>) =
        (LayoutInflater.from(context).inflate(R.layout.item_schedule_day, scheduleListHost, false)
                as LinearLayout).apply {
            id = 0x999999+dayIndex
            if (dayIndex == currentDayIndex) {
                setBackgroundColor(ContextCompat.getColor(context, R.color.currentDayBackground))
            }
            itemScheduleDayTitle.text = resources.getStringArray(R.array.days_of_week)[dayIndex]
            if (lessonsDay.isNullOrEmpty()) itemScheduleDayMessage.text = getString(R.string.no_lessons_today)
            val now = Date().time
            //Several lessons grouped by time range
            lessonsDay.forEach {  lessonPosition ->
                var lessonsCounter = 0
                // One lesson
                lessonPosition.forEach {lesson ->
                    val inRange = lesson.dateFrom < now && lesson.dateTo+1000*60*60*24 > now
                    if (scheduleTabLayout.selectedTabPosition == 1
                        || scheduleTabLayout.selectedTabPosition == 0 && inRange) {
                        val lessonItem = createLessonItem(context, lesson, currentScheduleType,
                            this, !inRange, lessonsCounter==0).apply {
                            setOnClickListener {
                                val objects = ArrayList<SearchObject>().apply {
                                    addAll(lesson.classrooms)
                                    addAll(lesson.teachers)
                                    if (currentScheduleType != ScheduleType.Group) add(lesson.group)
                                }
                                ScheduleDialogFragment
                                    .instance(objects, this@ScheduleFragment)
                                    .show(childFragmentManager, "ScheduleDialogFragment")
                            }
                        }
                        addView(lessonItem)
                        lessonsCounter++
                    }
                }
            }
    }

    companion object {
        /**
         * Inflates and fills lesson item view [R.layout.item_lesson]
         * @param lesson is [Lesson] object
         * @param parent is [ViewGroup] that should contain this view
         * @param hasBlur turns on semitransparent foreground view
         * @param hasTime turns on top line with time TextView
         * @return [View] [R.layout.item_lesson]
         */
        fun createLessonItem(
            context: Context, lesson: Lesson, scheduleType: ScheduleType, parent: ViewGroup?,
            hasBlur: Boolean, hasTime: Boolean
        ): View = LayoutInflater.from(context).inflate(R.layout.item_lesson, parent, false).apply {
                if (!hasTime) {
                    itemLessonTimeDivider.visibility = View.GONE
                } else {
                    itemLessonTimeDivider.visibility = View.VISIBLE
                    val time = context.resources.getStringArray(
                        if (lesson.group.isEvening) R.array.time_evening else R.array.time_morning
                    )[lesson.number]
                    itemLessonTime.text = time
                }
                if (scheduleType != ScheduleType.Group) {
                    itemLessonGroupDivider.visibility = View.VISIBLE
                    itemLessonGroup.text = lesson.group.name
                } else {
                    itemLessonGroupDivider.visibility = View.GONE
                }

                val classroomsString = SpannableStringBuilder().apply {
                    lesson.classrooms.forEach { classroom ->
                        if (classroom.name.startsWith("<a")
                            and classroom.name.contains("href")
                            and classroom.name.contains("</a>")){
                            append(HtmlCompat.fromHtml(classroom.name, HtmlCompat.FROM_HTML_MODE_LEGACY))
                        }else {
                            append(classroom.name).append(" ")
                            val colorSpan = try {
                                ForegroundColorSpan(Color.parseColor(classroom.color))
                            } catch (e: IllegalArgumentException) {
                                ForegroundColorSpan(Color.BLACK)
                            }
                            setSpan(
                                colorSpan, length - classroom.name.length - 1,
                                length, SPAN_INCLUSIVE_INCLUSIVE
                            )
                        }
                    }
                }
                if (classroomsString.isEmpty()) itemLessonClassrooms.visibility = View.GONE

                itemLessonClassrooms.apply {
                    movementMethod = LinkMovementMethod.getInstance()
                    text = classroomsString
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

    override fun onItemClick(query: String) {
        schedulePresenter.loadSchedule(query)
    }
}