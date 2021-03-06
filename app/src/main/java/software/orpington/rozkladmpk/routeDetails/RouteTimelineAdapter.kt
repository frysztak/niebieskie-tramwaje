package software.orpington.rozkladmpk.routeDetails

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.Timeline
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.android.synthetic.main.route_timeline_list_item.view.*

class RouteTimelineAdapter(private val context: Context) :
    RecyclerView.Adapter<RouteTimelineAdapter.ViewHolder>(),
    FastScrollRecyclerView.SectionedAdapter {

    private var highlightColour = ContextCompat.getColor(context, R.color.primary_dark)
    private var normalColour = ContextCompat.getColor(context, R.color.primary_text)

    private var itemIndexToHighlight: Int = -1
    private var items: List<Timeline.TimelineEntry> = emptyList()

    fun setItems(newItems: List<Timeline.TimelineEntry>, idx: Int) {
        items = newItems
        itemIndexToHighlight = idx
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
        return ViewHolder(view, highlightColour, normalColour)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.stopName.text = item.stopName
        holder.time.text = item.departureTime

        holder.verticalLineTopHalf.visibility = View.VISIBLE
        holder.verticalLineBottomHalf.visibility = View.VISIBLE
        when (position) {
            0 -> holder.verticalLineTopHalf.visibility = View.INVISIBLE
            itemCount - 1 -> holder.verticalLineBottomHalf.visibility = View.INVISIBLE
        }

        holder.onDemand.visibility = if (item.onDemand) View.VISIBLE else View.GONE

        if (position == itemIndexToHighlight) {
            holder.highlight()
        } else {
            holder.removeHighlight()
        }
    }

    override fun getSectionName(position: Int): String {
        val item = items[position]
        return item.departureTime
    }

    class ViewHolder(
        view: View,
        private val highlightColor: Int,
        private val normalColor: Int
    ) : RecyclerView.ViewHolder(view) {
        val stopName: TextView = view.stopName_textview
        val time: TextView = view.time_textview
        val verticalLineTopHalf: View = view.lineTopHalf
        val verticalLineBottomHalf: View = view.lineBottomHalf
        val onDemand: TextView = view.onDemand_textview

        fun highlight() {
            stopName.setTextColor(highlightColor)
        }

        fun removeHighlight() {
            stopName.setTextColor(normalColor)
        }
    }

}