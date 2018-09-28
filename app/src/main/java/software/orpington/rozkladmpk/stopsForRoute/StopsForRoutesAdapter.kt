package software.orpington.rozkladmpk.stopsForRoute

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import software.orpington.rozkladmpk.R
import kotlinx.android.synthetic.main.stops_and_routes_list_item.view.*

class StopsForRoutesAdapter(
    private val context: Context,
    private val presenter: StopsForRouteContract.Presenter
) : RecyclerView.Adapter<StopsForRoutesAdapter.ViewHolder>() {

    private var items = emptyList<String>()

    fun setItems(items: List<String>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.stops_and_routes_list_item, parent, false)
        return ViewHolder(view, presenter)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stopName = items[position]
        holder.iconImageView.setImageResource(R.drawable.bus_stop)
        holder.nameTextView.text = stopName
        holder.distanceTextView.visibility = View.GONE
    }

    class ViewHolder(
        val view: View,
        private val presenter: StopsForRouteContract.Presenter
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val nameTextView: TextView = view.mainName
        val iconImageView: ImageView = view.icon
        val distanceTextView: TextView = view.distance

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            presenter.stopClicked(adapterPosition)
        }
    }
}