package com.orpington.software.rozkladmpk.routeDetails

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orpington.software.rozkladmpk.R
import kotlinx.android.synthetic.main.route_directions_list_item.view.*

class RouteDirectionsAdapter(
    private val context: Context,
    private val presenter: RouteDetailsContract.Presenter
) : RecyclerView.Adapter<RouteDirectionsAdapter.ViewHolder>() {

    private var items: List<String> = emptyList()

    fun setItems(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.route_directions_list_item, parent, false)
        return ViewHolder(view, presenter)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.stopTextView.text = item
    }

    class ViewHolder(
        view: View,
        private val presenter: RouteDetailsContract.Presenter) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener {
        val stopTextView: TextView = view.stopName_textview

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            presenter.directionClicked(adapterPosition)
        }
    }
}