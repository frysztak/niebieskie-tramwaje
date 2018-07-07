package com.orpington.software.rozkladmpk.stopsAndRoutes

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.orpington.software.rozkladmpk.R
import kotlinx.android.synthetic.main.stops_and_routes_list_item.view.*

class StopsAndRoutesRecyclerViewAdapter(
    private val context: Context,
    private val presenter: StopsAndRoutesPresenter) :
    RecyclerView.Adapter<StopsAndRoutesRecyclerViewAdapter.ViewHolder>() {

    private var stops: List<String> = emptyList()

    fun setStops(newStops: List<String>) {
        stops = newStops
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.stops_and_routes_list_item, parent, false)
        return ViewHolder(view, presenter)
    }

    override fun getItemCount(): Int {
        return stops.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = stops[position]
        holder.iconImageView.setImageResource(R.drawable.traffic_light)
        holder.mainNameTextView.text = item
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
}