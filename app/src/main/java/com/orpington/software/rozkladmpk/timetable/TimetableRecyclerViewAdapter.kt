package com.orpington.software.rozkladmpk.timetable

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.timetable.TimetablePresenter.*
import kotlinx.android.synthetic.main.timetable_list_header.view.*
import kotlinx.android.synthetic.main.timetable_list_row.view.*

typealias Row = MutableList<String>

class TimetableRecyclerViewAdapter(private val context: Context,
                                   private val presenter: TimetablePresenter)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<ViewItem> = listOf()

    class HeaderViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val mainTextView: TextView = view.mainText
        val additionalTextView: TextView = view.additionalText
    }

    class RowViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val hourTextView: TextView = view.hourTextView
        val linearLayout: LinearLayout = view.linearLayout
    }

    fun setItems(newItems: List<ViewItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type.code
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ViewType.HEADER.code -> {
                val viewHolder = holder as HeaderViewHolder
                val item = items[position] as HeaderItem
                viewHolder.mainTextView.text = when(item.dayType) {
                    DayType.Weekday -> "Weekday"
                    DayType.Saturday -> "Saturday"
                    DayType.Sunday -> "Sunday"
                }
                viewHolder.additionalTextView.text = item.additionalText
                viewHolder.additionalTextView.visibility =
                    if (item.additionalText.isEmpty()) View.GONE else View.VISIBLE
            }
            ViewType.ROW.code -> {
                val viewHolder = holder as RowViewHolder
                val item = items[position] as RowItem
                populateRowLayout(item.data, viewHolder.hourTextView, viewHolder.linearLayout)
            }
            else -> throw Exception("Unknown ViewType")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.HEADER.code -> {
                val view = LayoutInflater.from(context).inflate(R.layout.timetable_list_header, parent, false)
                HeaderViewHolder(view)
            }
            ViewType.ROW.code -> {
                val view = LayoutInflater.from(context).inflate(R.layout.timetable_list_row, parent, false)
                RowViewHolder(view)
            }
            else -> throw Exception("Unknown ViewType")
        }
    }

    private fun populateRowLayout(row: Row, hourTextView: TextView, layout: LinearLayout) {
        layout.removeAllViews()

        hourTextView.text = row[0]

        for (minutes in row.drop(1)) {
            val view = LayoutInflater.from(context).inflate(R.layout.timetable_list_item, layout, false) as TextView
            view.text = minutes
            layout.addView(view)
        }
    }


}

