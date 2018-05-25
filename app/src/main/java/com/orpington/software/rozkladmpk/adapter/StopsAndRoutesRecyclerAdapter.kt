package com.orpington.software.rozkladmpk.adapter

import com.orpington.software.rozkladmpk.R
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.transport_line_list_layout.*

open class RouteListItem(
    val name: String,
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
    }
}

class HeaderListItem(name: String, iconId: Int, additionalInfo: String)
    : RouteListItem(name, iconId, additionalInfo), ExpandableItem {
    private lateinit var expandableGroup: ExpandableGroup

    override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.ViewHolder, position: Int) {
        super.bind(viewHolder, position)

        viewHolder.itemView.setOnClickListener {
            expandableGroup.onToggleExpanded()
        }
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        this.expandableGroup = onToggleListener
    }
}
