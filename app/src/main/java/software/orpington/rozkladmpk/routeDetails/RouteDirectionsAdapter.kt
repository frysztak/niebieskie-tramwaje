package software.orpington.rozkladmpk.routeDetails

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.route_directions_list_item.view.*
import software.orpington.rozkladmpk.R


class RouteDirectionsAdapter(
    private val context: Context,
    private val presenter: RouteDetailsContract.Presenter
) : RecyclerView.Adapter<RouteDirectionsAdapter.ViewHolder>() {

    private var highlightColour = ContextCompat.getColor(context, R.color.primary_dark)
    private var normalColour = ContextCompat.getColor(context, R.color.primary_text)

    private var items: List<String> = emptyList()

    fun setItems(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }

    private var favourites: Set<Int> = emptySet()
    fun setFavourites(newItems: Set<Int>) {
        favourites = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.route_directions_list_item, parent, false)
        return ViewHolder(view, highlightColour, normalColour, presenter)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.stopTextView.text = item
        holder.favouriteIcon.setImageResource(when (favourites.contains(position)) {
            true -> R.drawable.ic_favorite_black_24dp
            false -> R.drawable.ic_favorite_border_black_24dp
        })
    }

    class ViewHolder(
        view: View,
        private val highlightColor: Int,
        private val normalColor: Int,
        private val presenter: RouteDetailsContract.Presenter) :
        RecyclerView.ViewHolder(view) {
        val stopTextView: TextView = view.stopName_textview
        val favouriteIcon: ImageView = view.favouriteIcon

        init {
            view.setOnClickListener {
                presenter.onDirectionClicked(adapterPosition)
            }
            favouriteIcon.setOnClickListener {
                presenter.onDirectionFavouriteClicked(adapterPosition)
            }
        }

        fun highlight() {
            stopTextView.setTextColor(highlightColor)
        }

        fun removeHighlight() {
            stopTextView.setTextColor(normalColor)
        }
    }
}