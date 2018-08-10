package com.orpington.software.rozkladmpk.stopsAndRoutes

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.model.StopsAndRoutes
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.android.synthetic.main.stops_and_routes_list_item.view.*

class StopsAndRoutesAdapter(
    private val context: Context,
    private val presenter: StopsAndRoutesPresenter) :
    RecyclerView.Adapter<StopsAndRoutesAdapter.ViewHolder>(),
    FastScrollRecyclerView.SectionedAdapter {

    private var items: List<ViewItem> = emptyList()

    fun setItems(stopsAndRoutes: StopsAndRoutes) {
        this.items = convertToViewItems(stopsAndRoutes)
        notifyDataSetChanged()
    }

    fun itemClicked(position: Int) {
        val item = items[position]
        when (item.type) {
            ViewType.STOP -> presenter.stopClicked((item as StopItem).stopName)
            ViewType.ROUTE -> presenter.routeClicked((item as RouteItem).routeID)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.stops_and_routes_list_item, parent, false)
        return ViewHolder(view, presenter)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type.code
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        var iconResId = -1
        var text = ""

        when (item.type) {
            ViewType.STOP -> {
                iconResId = R.drawable.bus_stop
                text = (item as StopItem).stopName
            }
            ViewType.ROUTE -> {
                val routeItem = item as RouteItem
                iconResId = if (routeItem.isBus) R.drawable.bus else R.drawable.tram
                // e.g. "Route 33"
                text = "${context.getString(R.string.route)} ${routeItem.routeID}"
            }
        }

        holder.iconImageView.setImageResource(iconResId)
        holder.mainNameTextView.text = text
    }

    override fun getSectionName(position: Int): String {
        val item = items[position]
        return when (item.type) {
            ViewType.STOP -> (item as StopItem).stopName
            ViewType.ROUTE -> (item as RouteItem).routeID
        }.first().toString()
    }

    private fun convertToViewItems(data: StopsAndRoutes): List<ViewItem> {
        val stopViewItems: MutableList<ViewItem> = data.stops.map { stop ->
            StopItem(stop)
        }.toMutableList()

        val routeViewItems: List<ViewItem> = data.routes.map { route ->
            RouteItem(route.routeID, route.isBus)
        }

        stopViewItems.addAll(routeViewItems)
        return stopViewItems
    }

    class ViewHolder(view: View, private val presenter: StopsAndRoutesPresenter) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        val iconImageView: ImageView = view.icon
        val mainNameTextView: TextView = view.mainName

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            presenter.listItemClicked(adapterPosition)
        }
    }

    enum class ViewType(val code: Int) {
        STOP(0),
        ROUTE(1)
    }

    interface ViewItem {
        val type: ViewType
    }

    class StopItem(val stopName: String) : ViewItem {
        override val type: ViewType = ViewType.STOP
    }

    class RouteItem(val routeID: String, val isBus: Boolean) : ViewItem {
        override val type: ViewType = ViewType.ROUTE
    }
}