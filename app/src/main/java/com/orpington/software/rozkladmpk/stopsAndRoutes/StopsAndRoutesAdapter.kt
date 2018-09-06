package com.orpington.software.rozkladmpk.stopsAndRoutes

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.orpington.software.rozkladmpk.R
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.android.synthetic.main.stops_and_routes_empty_item.view.*
import kotlinx.android.synthetic.main.stops_and_routes_list_header.view.*
import kotlinx.android.synthetic.main.stops_and_routes_list_item.view.*
import kotlinx.android.synthetic.main.stops_and_routes_location_item.view.*
import kotlin.math.roundToInt

internal interface ClickListener {
    fun itemClicked(index: Int)
    fun okButtonClicked()
    fun neverButtonClicked()
}

class StopsAndRoutesAdapter(
    private val context: Context,
    private val presenter: StopsAndRoutesContract.Presenter) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    FastScrollRecyclerView.SectionedAdapter,
    ClickListener {

    private var skeletonScreen: SkeletonScreen? = null

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

    private var nearbyStops: List<StopOrRoute>? = emptyList()
    fun setNearbyStops(data: List<StopOrRoute>?) {
        nearbyStops = data
        updateItems()
    }

    private fun updateItems() {
        val searchResultsSection: List<ViewItem> = if (searchResults.isEmpty()) {
            emptyList()
        } else {
            val header: ViewItem = ViewItem.Header(context.getString(R.string.search_results))
            listOf(header) + convertToViewItems(searchResults)
        }

        if (searchResultsSection.isNotEmpty()) {
            items = searchResultsSection
            notifyDataSetChanged()
            return
        }

        skeletonScreen?.hide()
        skeletonScreen = null

        var nearbyStopsSection: List<ViewItem> = emptyList()
        if (presenter.shouldShowNearbyStops()) {
            val header = listOf(ViewItem.Header(context.getString(R.string.nearby_stops)))

            nearbyStopsSection = when {
                presenter.shouldShowNearbyStopsPrompt() -> header + ViewItem.AskAboutNearbyStops
                nearbyStops == null -> header + ViewItem.NoNearbyStopsFound
                nearbyStops != null && nearbyStops!!.isEmpty() -> header + ViewItem.NearbyStopsLoading
                else -> header + convertToViewItems(nearbyStops!!)
            }
        }

        val stopsAndRoutesSection =
            listOf(ViewItem.Header(context.getString(R.string.stops_and_routes))) + convertToViewItems(stopsAndRoutes)

        items = searchResultsSection + nearbyStopsSection + stopsAndRoutesSection
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewItem.ViewType.HEADER.code -> {
                val view = LayoutInflater.from(context).inflate(R.layout.stops_and_routes_list_header, parent, false)
                ViewHolder.Header(view)
            }
            ViewItem.ViewType.ASK_ABOUT_NEARBY_STOPS.code -> {
                val view = LayoutInflater.from(context).inflate(R.layout.stops_and_routes_location_item, parent, false)
                ViewHolder.AskAboutNearbyStops(view, this)
            }
            ViewItem.ViewType.NEARBY_STOPS_LOADING.code -> {
                val view = LayoutInflater.from(context).inflate(R.layout.stops_and_routes_empty_item, parent, false)
                ViewHolder.NearbyStopsLoading(view, skeletonScreen)
            }
            ViewItem.ViewType.NO_NEARBY_STOPS_FOUND.code -> {
                val view = LayoutInflater.from(context).inflate(R.layout.stops_and_routes_nearby_stops_not_found, parent, false)
                ViewHolder.NoNearbyStopsFound(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.stops_and_routes_list_item, parent, false)
                ViewHolder.StopOrRoute(view, this)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ViewItem.Stop -> ViewItem.ViewType.STOP
            is ViewItem.Route -> ViewItem.ViewType.ROUTE
            is ViewItem.Header -> ViewItem.ViewType.HEADER
            is ViewItem.AskAboutNearbyStops -> ViewItem.ViewType.ASK_ABOUT_NEARBY_STOPS
            is ViewItem.NearbyStopsLoading -> ViewItem.ViewType.NEARBY_STOPS_LOADING
            is ViewItem.NoNearbyStopsFound -> ViewItem.ViewType.NO_NEARBY_STOPS_FOUND
        }.code
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (item) {
            is ViewItem.Stop -> {
                val iconResId = R.drawable.bus_stop

                (holder as ViewHolder.StopOrRoute).apply {
                    iconImageView.setImageResource(iconResId)
                    mainNameTextView.text = item.stopName

                    distanceTextView.visibility = View.GONE
                    if (!item.distance.isNaN()) {
                        distanceTextView.visibility = View.VISIBLE
                        distanceTextView.text = "%d m".format(item.distance.roundToInt())
                    }
                }
            }

            is ViewItem.Route -> {
                val iconResId = if (item.isBus) R.drawable.bus else R.drawable.tram
                // e.g. "Route 33"
                val text = "${context.getString(R.string.route)} ${item.routeID}"

                (holder as ViewHolder.StopOrRoute).apply {
                    iconImageView.setImageResource(iconResId)
                    mainNameTextView.text = text
                }
            }

            is ViewItem.Header -> {
                (holder as ViewHolder.Header).mainText.text = item.title
            }
        }

    }

    override fun getSectionName(position: Int): String {
        val item = items[position]
        return when (item) {
            is ViewItem.Stop -> item.stopName
            is ViewItem.Route -> item.routeID
            else -> " " // TODO?
        }.first().toString()
    }

    private fun convertToViewItems(data: List<StopOrRoute>): List<ViewItem> {
        return data.map { item ->
            when (item) {
                is Stop -> ViewItem.Stop(item.stopName, item.distance)
                is Route -> ViewItem.Route(item.routeID, item.isBus)
            }
        }
    }

    override fun itemClicked(index: Int) {
        val item = items[index]
        when (item) {
            is ViewItem.Stop -> presenter.stopClicked(item.stopName)
            is ViewItem.Route -> presenter.routeClicked(item.routeID)
        }
    }

    override fun okButtonClicked() {
        presenter.agreeToLocationTrackingClicked()
    }

    override fun neverButtonClicked() {
        presenter.neverAskAboutLocationTrackingClicked()
    }

    internal sealed class ViewHolder {
        class StopOrRoute(view: View, private val clickListener: ClickListener) :
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

        class Header(view: View) : RecyclerView.ViewHolder(view) {
            val mainText: TextView = view.mainText
        }

        class AskAboutNearbyStops(view: View, private val clickListener: ClickListener) :
            RecyclerView.ViewHolder(view) {
            val okButton: Button = view.okButton
            val neverButton: Button = view.neverButton

            init {
                okButton.setOnClickListener { clickListener.okButtonClicked() }
                neverButton.setOnClickListener { clickListener.neverButtonClicked() }
            }
        }

        class NearbyStopsLoading(view: View, private var skeletonScreen: SkeletonScreen?) :
            RecyclerView.ViewHolder(view) {
            private val rootView: View = view.root

            init {
                skeletonScreen = Skeleton.bind(rootView)
                    .load(R.layout.stops_and_routes_skeleton_list_item)
                    .show()
            }
        }

        class NoNearbyStopsFound(view: View) : RecyclerView.ViewHolder(view)
    }

    internal sealed class ViewItem {
        enum class ViewType(val code: Int) {
            HEADER(0),
            STOP(1),
            ROUTE(2),
            NEARBY_STOPS_LOADING(3),
            ASK_ABOUT_NEARBY_STOPS(4),
            NO_NEARBY_STOPS_FOUND(5)
        }

        class Header(val title: String) : ViewItem()
        class Stop(val stopName: String, val distance: Float) : ViewItem()
        class Route(val routeID: String, val isBus: Boolean) : ViewItem()

        object NearbyStopsLoading : ViewItem()
        object AskAboutNearbyStops : ViewItem()
        object NoNearbyStopsFound : ViewItem()
    }
}