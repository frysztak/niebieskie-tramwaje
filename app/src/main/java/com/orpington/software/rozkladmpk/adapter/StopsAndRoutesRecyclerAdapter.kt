package com.orpington.software.rozkladmpk.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.presenter.BindingPresenter
import com.orpington.software.rozkladmpk.presenter.StopsAndRoutesPresenter
import kotlinx.android.synthetic.main.transport_line_list_layout.view.*

interface RowView {
    fun setIcon(resId: Int)
    fun setName(name: String)
    fun setAdditionalText(text: String)
}

interface RecyclerViewClickListener {
    fun onClick(view: View, position: Int)
}

class ViewHolder(
   view: View,
   private var clickListener: RecyclerViewClickListener
): RecyclerView.ViewHolder(view),
    RowView,
   View.OnClickListener
{
    val icon: ImageView = view.icon
    val mainName: TextView = view.mainName
    val additionalText: TextView = view.additionalText

    init {
        view.setOnClickListener(this)
    }

    override fun setIcon(resId: Int) {
        icon.setImageResource(resId)
    }

    override fun setName(name: String) {
        mainName.text = name
    }

    override fun setAdditionalText(text: String) {
        additionalText.text = text
    }

    override fun onClick(view: View) {
        clickListener.onClick(view, adapterPosition)
    }
}

class StopsAndRoutesRecyclerAdapter(
    private var presenter: BindingPresenter,
    private var clickListener: RecyclerViewClickListener
): RecyclerView.Adapter<ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        return ViewHolder(layoutInflater.inflate(R.layout.transport_line_list_layout, parent, false), clickListener)
    }

    override fun getItemCount(): Int {
        return presenter.getSize()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        presenter.onBindTransportLineRowViewAtPosition(position, holder)
    }

    override fun getItemViewType(position: Int): Int {
        return presenter.getItemViewType(position)
    }
}