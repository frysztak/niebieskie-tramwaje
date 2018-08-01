package com.orpington.software.rozkladmpk.routeDetails

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.model.Timeline
import kotlinx.android.synthetic.main.route_timeline_list_item.view.*

class RouteTimelineAdapter(private val context: Context)
    : RecyclerView.Adapter<RouteTimelineAdapter.ViewHolder>() {

    private var items: List<Timeline.TimelineEntry> = emptyList()

    fun setItems(newItems: List<Timeline.TimelineEntry>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.route_timeline_list_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.stopName.text = item.stopName
        holder.time.text = item.departureTime.removeSuffix(":00")

        holder.verticalLineTopHalf.visibility = View.VISIBLE
        holder.verticalLineBottomHalf.visibility = View.VISIBLE
        when (position) {
            0 -> holder.verticalLineTopHalf.visibility = View.INVISIBLE
            itemCount - 1 -> holder.verticalLineBottomHalf.visibility = View.INVISIBLE
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stopName: TextView = view.stopName_textview
        val time: TextView = view.time_textview
        val verticalLineTopHalf: View = view.lineTopHalf
        val verticalLineBottomHalf: View = view.lineBottomHalf
    }

}