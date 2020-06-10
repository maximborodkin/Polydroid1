package ru.maxim.mospolytech.polydroid.ui.activity.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_notification.view.*
import ru.maxim.mospolytech.polydroid.R
import ru.maxim.mospolytech.polydroid.model.Notification
import ru.maxim.mospolytech.polydroid.model.ScheduleType
import ru.maxim.mospolytech.polydroid.ui.activity.notifications.NotificationsRecyclerAdapter.NotificationViewHolder
import ru.maxim.mospolytech.polydroid.ui.fragment.schedule.ScheduleFragment
import ru.maxim.mospolytech.polydroid.util.DateFormatUtils

class NotificationsRecyclerAdapter(val notifications: List<Notification>)
    : RecyclerView.Adapter<NotificationViewHolder>() {

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateView: TextView = view.itemNotificationDate
        val oldLessonView: LinearLayout = view.itemNotificationOldLesson
        val newLessonView: LinearLayout = view.itemNotificationNewLesson
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder =
        NotificationViewHolder(LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_notification, parent, false))

    override fun getItemCount(): Int = notifications.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        val context = holder.itemView.context
        holder.dateView.text = DateFormatUtils.simplifyDate(notification.time)
        val oldLesson = notification.getOldLesson()
        if (oldLesson != null) {
            holder.oldLessonView.addView(
                ScheduleFragment.createLessonItem(
                    context, oldLesson, ScheduleType.Group,
                    null, false, 0, true)
            )
        } else {
            holder.oldLessonView.addView(TextView(context).apply {
                    text = context.getString(R.string.no_lesson)
                    textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    textSize = 18F
                    setTextColor(ContextCompat.getColor(context, R.color.black))
            })
        }

        val newLesson = notification.getNewLesson()
        if (newLesson != null){
            holder.newLessonView.addView(
                ScheduleFragment.createLessonItem(
                    context, newLesson, ScheduleType.Group,
                    null, false, 0, true)
            )
        } else {
            holder.newLessonView.addView(TextView(context).apply {
                text = context.getString(R.string.no_lesson)
                    textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    textSize = 18F
                    setTextColor(ContextCompat.getColor(context, R.color.black))
            })
        }
    }
}