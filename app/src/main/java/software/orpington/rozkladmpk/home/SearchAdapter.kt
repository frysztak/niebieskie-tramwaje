package software.orpington.rozkladmpk.home

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.stops_and_routes_list_item.view.*
import software.orpington.rozkladmpk.R

internal interface ClickListener {
    fun itemClicked(index: Int)
}

class SearchAdapter(
    private val context: Context,
    private val presenter: HomeFragmentContract.Presenter) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ClickListener {


    private var items: List<ViewItem> = emptyList()

    fun setItems(data: List<StopOrRoute>) {
        items = convertToViewItems(data)
        notifyDataSetChanged()
    }

    private fun convertToViewItems(data: List<StopOrRoute>): List<ViewItem> {
        return data.map { item ->
            when (item) {
                is Stop -> ViewItem.Stop(item.stopName)
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

    override fun getItemCount(): Int = items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.stops_and_routes_list_item, parent, false)
        return StopOrRouteViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (item) {
            is ViewItem.Stop -> {
                val iconResId = R.drawable.bus_stop

                (holder as StopOrRouteViewHolder).apply {
                    iconImageView.setImageResource(iconResId)
                    mainNameTextView.text = item.stopName
                    distanceTextView.visibility = View.GONE
                }
            }

            is ViewItem.Route -> {
                val iconResId = if (item.isBus) R.drawable.bus else R.drawable.tram
                // e.g. "Route 33"
                val text = "${context.getString(R.string.route)} ${item.routeID}"

                (holder as StopOrRouteViewHolder).apply {
                    iconImageView.setImageResource(iconResId)
                    mainNameTextView.text = text
                    distanceTextView.visibility = View.GONE
                }
            }
        }
    }

    internal sealed class ViewItem {
        enum class ViewType(val code: Int) {
            STOP(1),
            ROUTE(2),
        }

        class Stop(val stopName: String) : ViewItem()
        class Route(val routeID: String, val isBus: Boolean) : ViewItem()
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
}