package software.orpington.rozkladmpk.home.map

import android.content.Context
import android.support.constraint.Group
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.home_map_nearby_header.view.*
import kotlinx.android.synthetic.main.home_map_nearby_item.view.*
import software.orpington.rozkladmpk.R

class DeparturesAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<DepartureViewItem> = emptyList()
    override fun getItemCount(): Int = items.size

    fun setItems(data: List<DepartureViewItem>) {
        items = data
        notifyDataSetChanged()
    }

    private val ITEM_TYPE_HEADER = 0
    private val ITEM_TYPE_DETAILS = 1

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when (item) {
            is DepartureHeader -> ITEM_TYPE_HEADER
            is DepartureDetails -> ITEM_TYPE_DETAILS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_HEADER -> {
                val view = LayoutInflater.from(context).inflate(R.layout.home_map_nearby_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.home_map_nearby_item, parent, false)
                DetailsViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (item) {
            is DepartureHeader -> (holder as HeaderViewHolder).apply {
                stopName.text = item.stopName
                distance.text = "%.1f m".format(item.distance)
            }
            is DepartureDetails -> (holder as DetailsViewHolder).apply {
                routeID.text = item.routeID
                direction.text = item.direction
                departureTime.text = getDepartureInString(item.departureInMinutes, item.departureTime)
                onDemand.visibility = if (item.onDemand) View.VISIBLE else View.GONE
            }
        }
    }

    private fun getDepartureInString(departureInMinutes: Int, departureTime: String): String {
        val minutesString = context.getString(when (departureInMinutes) {
            1 -> R.string.minute_1
            in 2..4 -> R.string.minutes_2to4
            else -> R.string.minutes_5plus
        })

        val localisedTemplate = context.getString(R.string.departureIn)
        return localisedTemplate.format(departureInMinutes, minutesString, departureTime)
    }
}

class HeaderViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {
    val stopName: TextView = view.stopName
    val distance: TextView = view.distance
}

class DetailsViewHolder(
    private val view: View
) : RecyclerView.ViewHolder(view) {
    val vehicleIcon: ImageView = view.vehicleIcon
    val routeID: TextView = view.routeID
    val direction: TextView = view.direction
    val departureTime: TextView = view.departureTime
    val onDemand: Group = view.onDemand
}
