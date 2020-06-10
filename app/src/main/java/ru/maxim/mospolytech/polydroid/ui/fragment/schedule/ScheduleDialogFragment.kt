package ru.maxim.mospolytech.polydroid.ui.fragment.schedule

import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.marginBottom
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_schedule_dialog.*
import ru.maxim.mospolytech.polydroid.R
import ru.maxim.mospolytech.polydroid.model.SearchObject

class ScheduleDialogFragment : DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_schedule_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        objects.forEach {
            val objectView = AppCompatTextView(context).apply {
                text = it.name
                val icon = when(it.getType()) {
                    "group" -> R.drawable.ic_group_gray_24dp
                    "teacher" -> R.drawable.ic_teacher_gray_24dp
                    "classroom" -> R.drawable.ic_door_gray_24dp
                    else -> 0
                }
                setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
                val query = "schedule/${it.getType()}/${it.id}"
                setOnClickListener {
                    onItemClickListener.onItemClick(query)
                    dialog?.dismiss()
                }
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10F,
                    resources.displayMetrics).toInt()
                )}
            }
            scheduleDialogItemsHolder.addView(objectView)
        }
    }

    companion object {
        private lateinit var objects: List<SearchObject>
        private lateinit var onItemClickListener: OnItemClickListener

        fun instance(objects: List<SearchObject>, onItemClickListener: OnItemClickListener): ScheduleDialogFragment {
            this.objects = objects
            this.onItemClickListener = onItemClickListener
            return ScheduleDialogFragment()
        }
    }

    interface OnItemClickListener {
        fun onItemClick(query: String)
    }
}