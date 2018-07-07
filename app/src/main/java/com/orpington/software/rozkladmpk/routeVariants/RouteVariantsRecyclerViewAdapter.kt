package com.orpington.software.rozkladmpk.routeVariants

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.model.RouteVariant
import kotlinx.android.synthetic.main.route_card_view.view.*

class RouteVariantsRecyclerViewAdapter(
    private val context: Context) :
    RecyclerView.Adapter<RouteVariantsRecyclerViewAdapter.ViewHolder>() {

    private var items: List<RouteVariant> = emptyList()

    fun setItems(newItems: List<RouteVariant>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.route_card_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.iconImageView.setImageResource(when (item.isBus) {
            true -> R.drawable.bus
            else -> R.drawable.train
        })
        holder.nameTextView.text = item.routeID
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.name
        val iconImageView: ImageView = view.icon
    }
}