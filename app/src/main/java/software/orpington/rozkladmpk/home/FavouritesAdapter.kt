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

    private var items: List<FavouriteViewModel> = emptyList()
    fun setItems(newItems: List<FavouriteViewModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    internal interface ClickListener {
        fun itemClicked(index: Int)
    }

    private val ITEM_TYPE_REGULAR = 0
    private val ITEM_TYPE_ADD_NEW = 1

    override fun getItemCount(): Int = items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val clickListener = object : ClickListener {
            override fun itemClicked(index: Int) = presenter.favouriteClicked(index)
        }

        return when (viewType) {
            ITEM_TYPE_REGULAR -> {
                val view = LayoutInflater.from(context).inflate(R.layout.home_favourite_card, parent, false)
                FavouriteViewHolder(view, clickListener)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.home_favourite_add_new, parent, false)
                FavouriteAddNewViewHolder(view, clickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when (item) {
            is FavouriteItem -> ITEM_TYPE_REGULAR
            is FavouriteAddNew -> ITEM_TYPE_ADD_NEW
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (item) {
            is FavouriteItem -> {
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

    internal class FavouriteAddNewViewHolder(
        view: View,
        private val clickListener: ClickListener
    ) :
        RecyclerView.ViewHolder(view) {

        init {
            view.setOnClickListener {
                clickListener.itemClicked(adapterPosition)
            }
        }
    }
}