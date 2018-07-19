package com.orpington.software.rozkladmpk.routeVariants

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.model.RouteVariant
import kotlinx.android.synthetic.main.route_variant_list_item.view.*

class VariantsRecyclerViewAdapter(
    private val context: Context,
    private val presenter: RouteVariantsPresenter) :
    RecyclerView.Adapter<VariantsRecyclerViewAdapter.ViewHolder>() {

    private var items: List<RouteVariant> = emptyList()

    fun setItems(newItems: List<RouteVariant>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.route_variant_list_item, parent, false)
        return ViewHolder(view, presenter)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.firstTextView.text = item.firstStop
        holder.lastTextView.text = item.lastStop
    }

    class ViewHolder(
        view: View,
        private val presenter: RouteVariantsPresenter
    ) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        val firstTextView: TextView = view.firstStopName
        val lastTextView: TextView = view.lastStopName

        init {
            view.findViewById<ConstraintLayout>(R.id.root)?.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            presenter.variantClicked(adapterPosition)
        }
    }
}