package ru.maxim.mospolytech.polydroid.ui.fragment.schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import kotlinx.android.synthetic.main.item_search.view.*
import ru.maxim.mospolytech.polydroid.R
import ru.maxim.mospolytech.polydroid.model.SearchObject

class SearchArrayAdapter(context: Context, items: List<SearchObject>) : ArrayAdapter<SearchObject>(context, 0, ArrayList()) {

    private val searchItemsFull: ArrayList<SearchObject> = ArrayList(items) // copy of original list

    private val searchFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            val suggestions = ArrayList<SearchObject>()
            if (!constraint.isNullOrEmpty()) {
                searchItemsFull.forEach {
                    if (it.hasEntry(constraint.toString())) suggestions.add(it)
                }
            }
            filterResults.values = suggestions
            filterResults.count = suggestions.size
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            clear()
            @Suppress("UNCHECKED_CAST")
            addAll(results.values as List<SearchObject>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?) = resultValue.toString()
    }

    override fun getFilter() = searchFilter

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: SearchObjectViewHolder
        val view: View
        val item = getItem(position)
        val icon = when(item?.getType()){
            "group" -> R.drawable.ic_group_gray_24dp
            "teacher" -> R.drawable.ic_teacher_gray_24dp
            "classroom" -> R.drawable.ic_door_gray_24dp
            else -> 0
        }

        if(convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false)
            holder = SearchObjectViewHolder()
            holder.titleView = view.itemSearchTitle.apply { text = item?.name }
            holder.iconView = view.itemSearchIcon.apply { setBackgroundResource(icon) }
            view.tag = holder
        } else {
            holder = convertView.tag as SearchObjectViewHolder
            holder.titleView?.text = item?.name
            holder.iconView?.setBackgroundResource(icon)
            view = convertView
        }
        return view
    }

    internal class SearchObjectViewHolder {
        var titleView: TextView? = null
        var iconView: AppCompatImageView? = null
    }
}