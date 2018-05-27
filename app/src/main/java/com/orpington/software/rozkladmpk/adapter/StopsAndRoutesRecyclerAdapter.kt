package com.orpington.software.rozkladmpk.adapter

import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View
import com.orpington.software.rozkladmpk.R
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_list_layout.*


open class RouteListItem(
    val name: String,
    private val iconId: Int,
    private val additionalInfo: String
) : Item() {
    override fun getLayout() = R.layout.item_list_layout

    override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.ViewHolder, position: Int) {
        with(viewHolder) {
            mainName.text = name
            icon.setImageResource(iconId)
            additionalText.text = additionalInfo
            expandIcon.visibility = View.GONE
        }
    }
}

class HeaderListItem(name: String, iconId: Int, additionalInfo: String)
    : RouteListItem(name, iconId, additionalInfo), ExpandableItem {
    private lateinit var expandableGroup: ExpandableGroup

    override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.expandIcon.visibility = View.VISIBLE
        bindIcon(viewHolder, false)

        viewHolder.itemView.setOnClickListener {
            expandableGroup.onToggleExpanded()
            bindIcon(viewHolder, true)
        }
    }

    private fun bindIcon(viewHolder: ViewHolder, animate: Boolean) {
        viewHolder.expandIcon.setImageResource(
            if (expandableGroup.isExpanded)
                R.drawable.expand_to_collapse_anim else R.drawable.collapse_to_expand_anim
        )

        if (animate) {
            (viewHolder.expandIcon.drawable as AnimatedVectorDrawable).start()
        }
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        this.expandableGroup = onToggleListener
    }
}
