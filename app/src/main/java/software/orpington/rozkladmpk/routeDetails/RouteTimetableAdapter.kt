package software.orpington.rozkladmpk.routeDetails

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.routeDetails.TimetableViewHelper.*
import kotlinx.android.synthetic.main.route_timetable_list_header.view.*
import kotlinx.android.synthetic.main.route_timetable_list_item.view.*

typealias Row = MutableList<String>

class RouteTimetableAdapter(
    private val context: Context,
    private val presenter: RouteDetailsContract.Presenter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<ViewItem> = listOf()

    class HeaderViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val mainTextView: TextView = view.mainText
        val additionalTextView: TextView = view.additionalText
    }

    class RowViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val rootLayout: LinearLayout = view.rootLayout
        val hourTextView: TextView = view.hourTextView
        val minutesLayout: LinearLayout = view.minutesLayout
    }

    fun setItems(newItems: List<ViewItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun getItem(idx: Int): ViewItem {
        return items[idx]
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
                viewHolder.mainTextView.text = when (item.dayType) {
                    DayType.Weekday -> context.getString(R.string.weekdays)
                    DayType.Saturday -> context.getString(R.string.saturday)
                    DayType.Sunday -> context.getString(R.string.sunday)
                }
                viewHolder.additionalTextView.visibility =
                    if (item.today) View.VISIBLE else View.GONE
            }
            ViewType.ROW.code -> {
                val viewHolder = holder as RowViewHolder
                val item = items[position] as RowItem
                populateRowLayout(item, viewHolder)
            }
            else -> throw Exception("Unknown ViewType")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.HEADER.code -> {
                val view = LayoutInflater.from(context).inflate(R.layout.route_timetable_list_header, parent, false)
                HeaderViewHolder(view)
            }
            ViewType.ROW.code -> {
                val view = LayoutInflater.from(context).inflate(R.layout.route_timetable_list_item, parent, false)
                RowViewHolder(view)
            }
            else -> throw Exception("Unknown ViewType")
        }
    }

    private fun populateRowLayout(row: RowItem, vh: RowViewHolder) {
        vh.minutesLayout.removeAllViews()

        val hour = row.data[0]
        vh.hourTextView.text = hour

        for (minutes in row.data.drop(1)) {
            val view = LayoutInflater.from(context).inflate(R.layout.route_timetable_minute, vh.minutesLayout, false) as TextView
            view.text = minutes
            view.tag = "${row.dayType.prefix}:$hour:$minutes"

            view.setOnClickListener {
                presenter.onTimeClicked(it.tag as String)
            }

            vh.minutesLayout.addView(view)
        }
        vh.rootLayout.tag = "${row.dayType.prefix}:$hour"
    }


}

