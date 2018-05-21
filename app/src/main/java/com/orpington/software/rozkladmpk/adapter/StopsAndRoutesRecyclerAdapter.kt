package com.orpington.software.rozkladmpk.adapter

import android.view.View
import com.orpington.software.rozkladmpk.R
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.transport_line_list_layout.*

/*
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
*/

open class RouteListItem(
    private val name: String,
    private val iconId: Int,
    private val additionalInfo: String
): Item() {
    override fun getLayout() = R.layout.transport_line_list_layout

    override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.ViewHolder, position: Int) {
        with(viewHolder) {
            mainName.text = name
            icon.setImageResource(iconId)
            additionalText.text = additionalInfo
        }

        viewHolder.itemView.setOnClickListener(onClickListener)
    }

    var onClickListener: View.OnClickListener? = null
}

class HeaderListItem(
    private val name: String,
    private val iconId: Int,
    private val additionalInfo: String
): RouteListItem(name, iconId, additionalInfo), ExpandableItem {
    private lateinit var expandableGroup: ExpandableGroup

    init {
        onClickListener = View.OnClickListener { expandableGroup.onToggleExpanded() }
    }

    override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.ViewHolder, position: Int) {
        super.bind(viewHolder, position)
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        this.expandableGroup = onToggleListener
    }
}

/*
class StopsAndRoutesRecyclerAdapter(
    private var presenter: BindingPresenter,
    private var clickListener: RecyclerViewClickListener
): RecyclerView.Adapter<ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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
    */