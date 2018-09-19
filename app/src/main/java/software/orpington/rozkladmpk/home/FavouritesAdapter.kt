package software.orpington.rozkladmpk.home

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.home_favourite_card.view.*
import software.orpington.rozkladmpk.R


class FavouritesAdapter(
    private val context: Context,
    private val presenter: FavouritesContract.Presenter
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<FavouriteItem> = emptyList()
    fun setItems(newItems: List<FavouriteItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    internal interface ClickListener {
        fun itemClicked(index: Int)
    }

    override fun getItemCount(): Int = items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.home_favourite_card, parent, false)
        val clickListener = object : ClickListener {
            override fun itemClicked(index: Int) {
                presenter.favouriteClicked(index)
            }
        }

        return FavouriteViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        (holder as FavouriteViewHolder).apply {
            icon.setImageResource(when (item.isBus) {
                true -> R.drawable.ic_bus_white_24dp
                false -> R.drawable.ic_tram_white_24dp
            })
            routeID.text = item.routeID
            direction.text = item.direction
            stop.text = item.stopName
        }
    }


    internal class FavouriteViewHolder(
        view: View,
        private val clickListener: ClickListener
    ) :
        RecyclerView.ViewHolder(view) {

        val icon: ImageView = view.favouriteCard_icon
        val routeID: TextView = view.favouriteCard_lineNumber
        val direction: TextView = view.favouriteCard_direction
        val stop: TextView = view.favouriteCard_stop

        init {
            view.setOnClickListener {
                clickListener.itemClicked(adapterPosition)
            }
        }
    }
}