package software.orpington.rozkladmpk.home.map

import android.content.Context
import android.content.res.ColorStateList
import android.support.constraint.Group
import android.support.v4.content.ContextCompat
import android.support.v4.widget.ImageViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.home_map_nearby_header.view.*
import kotlinx.android.synthetic.main.home_map_nearby_item.view.*
import kotlinx.android.synthetic.main.home_map_nearby_show_more.view.*
import software.orpington.rozkladmpk.R

class DeparturesAdapter(
    private val context: Context,
    private val presenter: MapPresenter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<DepartureViewItem> = emptyList()
    override fun getItemCount(): Int = items.size

    fun setItems(data: List<DepartureViewItem>) {
        items = data
        notifyDataSetChanged()
    }

    private val ITEM_TYPE_HEADER = 0
    private val ITEM_TYPE_DETAILS = 1
    private val ITEM_TYPE_SHOW_MORE = 2

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when (item) {
            is DepartureHeader -> ITEM_TYPE_HEADER
            is DepartureDetails -> ITEM_TYPE_DETAILS
            is DepartureShowMore -> ITEM_TYPE_SHOW_MORE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_HEADER -> {
                val view = LayoutInflater.from(context).inflate(R.layout.home_map_nearby_header, parent, false)
                HeaderViewHolder(view)
            }
            ITEM_TYPE_DETAILS -> {
                val view = LayoutInflater.from(context).inflate(R.layout.home_map_nearby_item, parent, false)
                DetailsViewHolder(view, presenter)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.home_map_nearby_show_more, parent, false)
                ShowMoreViewHolder(view, presenter)
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
                vehicleIcon.setImageResource(when (item.isBus) {
                    true -> R.drawable.ic_bus_white_24dp
                    false -> R.drawable.ic_tram_white_24dp
                })
                routeID.text = item.routeID
                direction.text = item.direction
                departureTime.text = getDepartureInString(item.departureInMinutes, item.departureTime)
                onDemand.visibility = if (item.onDemand) View.VISIBLE else View.GONE

                val regularColour = ContextCompat.getColor(context, R.color.primary_text)
                val tintColor = if (item.isTracked && item.shapeColour != -1) item.shapeColour else regularColour
                ImageViewCompat.setImageTintList(trackIcon, ColorStateList.valueOf(tintColor))
            }
        }
    }

    private fun getDepartureInString(departureInMinutes: Int, departureTime: String): String {
        val getMinutesString = { minutes: Int ->
            val str = context.getString(when (minutes) {
                1 -> R.string.minute_1
                in 2..4 -> R.string.minutes_2to4
                in 5..59 -> R.string.minutes_5plus
                60 -> R.string.hour
                else -> R.string.over_hour
            })

            when (minutes) {
                in 2..59 -> "%d %s".format(minutes, str)
                else -> str
            }
        }

        if (departureInMinutes < 0) {
            val localisedTemplate = context.getString(R.string.already_departed)
            return localisedTemplate.format(getMinutesString(-departureInMinutes), departureTime)
        }

        if (departureInMinutes == 0) {
            val localisedTemplate = context.getString(R.string.departs_now)
            return localisedTemplate.format(departureTime)
        }

        val localisedTemplate = context.getString(R.string.departureIn)
        return localisedTemplate.format(getMinutesString(departureInMinutes), departureTime)
    }
}

internal class HeaderViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {
    val stopName: TextView = view.stopName
    val distance: TextView = view.distance
}

internal class DetailsViewHolder(
    view: View,
    private val presenter: MapPresenter
) : RecyclerView.ViewHolder(view) {
    val vehicleIcon: ImageView = view.vehicleIcon
    val routeID: TextView = view.routeID
    val direction: TextView = view.direction
    val trackIcon: ImageView = view.trackIcon
    val departureTime: TextView = view.departureTime
    val onDemand: Group = view.onDemand

    init {
        trackIcon.setOnClickListener {
            presenter.onTrackButtonClicked(adapterPosition)
        }
    }
}

internal class ShowMoreViewHolder(
    view: View,
    private val presenter: MapPresenter
) : RecyclerView.ViewHolder(view) {
    private val showMoreText: TextView = view.showMore

    init {
        showMoreText.setOnClickListener {
            presenter.onShowMoreClicked(adapterPosition)
        }
    }
}
