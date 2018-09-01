package com.orpington.software.rozkladmpk.stopsAndRoutes

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.orpington.software.rozkladmpk.R
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.android.synthetic.main.stops_and_routes_list_header.view.*
import kotlinx.android.synthetic.main.stops_and_routes_list_item.view.*
import kotlin.math.roundToInt

internal interface ClickListener {
    fun itemClicked(index: Int)
}

class StopsAndRoutesAdapter(
    private val context: Context,
    private val presenter: StopsAndRoutesPresenter) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    FastScrollRecyclerView.SectionedAdapter,
    ClickListener {

    private var items: List<ViewItem> = emptyList()

    private var searchResults: List<StopOrRoute> = emptyList()
    fun setSearchResults(data: List<StopOrRoute>) {
        searchResults = data
        updateItems()
    }

    private var stopsAndRoutes: List<StopOrRoute> = emptyList()
    fun setStopsAndRoutes(data: List<StopOrRoute>) {
        stopsAndRoutes = data
        updateItems()
    }

    private var nearbyStops: List<StopOrRoute> = emptyList()
    fun setNearbyStops(data: List<StopOrRoute>) {
        nearbyStops = data
        updateItems()
    }

    private fun updateItems() {
        val searchResultsSection: List<ViewItem> = if (searchResults.isEmpty()) {
            emptyList()
        } else {
            val header: ViewItem = HeaderItem(context.getString(R.string.search_results))
            listOf(header) + convertToViewItems(searchResults)
        }

        val nearbyStopsSection: List<ViewItem> = if (nearbyStops.isEmpty()) {
            emptyList()
        } else {
            val header: ViewItem = HeaderItem(context.getString(R.string.nearby_stops))
            listOf(header) + convertToViewItems(nearbyStops)
        }

        val stopsAndRoutesSection =
            listOf(HeaderItem(context.getString(R.string.stops_and_routes))) + convertToViewItems(stopsAndRoutes)

        items = searchResultsSection + nearbyStopsSection + stopsAndRoutesSection
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.HEADER.code -> {
                val view = LayoutInflater.from(context).inflate(R.layout.stops_and_routes_list_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.stops_and_routes_list_item, parent, false)
                StopOrRouteViewHolder(view, this)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type.code
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (item.type) {
            ViewType.STOP -> {
                val iconResId = R.drawable.bus_stop
                val stop = item as StopItem

                (holder as StopOrRouteViewHolder).apply {
                    iconImageView.setImageResource(iconResId)
                    mainNameTextView.text = stop.stopName

                    distanceTextView.visibility = View.GONE
                    if (!stop.distance.isNaN()) {
                        distanceTextView.visibility = View.VISIBLE
                        distanceTextView.text = "%d m".format(stop.distance.roundToInt())
                    }
                }
            }

            ViewType.ROUTE -> {
                val routeItem = item as RouteItem
                val iconResId = if (routeItem.isBus) R.drawable.bus else R.drawable.tram
                // e.g. "Route 33"
                val text = "${context.getString(R.string.route)} ${routeItem.routeID}"

                (holder as StopOrRouteViewHolder).apply {
                    iconImageView.setImageResource(iconResId)
                    mainNameTextView.text = text
                }
            }

            ViewType.HEADER -> {
                val header = item as HeaderItem
                (holder as HeaderViewHolder).mainText.text = header.title
            }
        }

    }

    override fun getSectionName(position: Int): String {
        val item = items[position]
        return when (item.type) {
            ViewType.STOP -> (item as StopItem).stopName
            ViewType.ROUTE -> (item as RouteItem).routeID
            ViewType.HEADER -> " " // TODO?
        }.first().toString()
    }

    private fun convertToViewItems(data: List<StopOrRoute>): List<ViewItem> {
        return data.map { item ->
            when (item) {
                is Stop -> StopItem(item.stopName, item.distance)
                is Route -> RouteItem(item.routeID, item.isBus)
            }
        }
    }

    internal class StopOrRouteViewHolder(view: View, private val clickListener: ClickListener) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        val iconImageView: ImageView = view.icon
        val mainNameTextView: TextView = view.mainName
        val distanceTextView: TextView = view.distance

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener.itemClicked(adapterPosition)
        }
    }

    override fun itemClicked(index: Int) {
        val item = items[index]
        when (item.type) {
            ViewType.STOP -> presenter.stopClicked((item as StopItem).stopName)
            ViewType.ROUTE -> presenter.routeClicked((item as RouteItem).routeID)
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainText: TextView = view.mainText
    }

    enum class ViewType(val code: Int) {
        HEADER(0),
        STOP(1),
        ROUTE(2)
    }

    interface ViewItem {
        val type: ViewType
    }

    class HeaderItem(val title: String) : ViewItem {
        override val type: ViewType = ViewType.HEADER
    }

    class StopItem(val stopName: String, val distance: Float) : ViewItem {
        override val type: ViewType = ViewType.STOP
    }

    class RouteItem(val routeID: String, val isBus: Boolean) : ViewItem {
        override val type: ViewType = ViewType.ROUTE
    }
}