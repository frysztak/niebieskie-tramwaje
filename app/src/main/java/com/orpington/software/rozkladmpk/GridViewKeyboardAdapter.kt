package com.orpington.software.rozkladmpk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


/**
 * Created by sebastian on 3/28/18.
 */

class GridViewKeyboardAdapter(c: Context, i: List<String>) : BaseAdapter() {
    private var mContext: Context = c
    private var items: List<String> = i

    override fun getView(position: Int, initialConvertView: View?, parentView: ViewGroup?): View? {
        val item = items[position]
        var convertView = initialConvertView

        if (initialConvertView == null) {
            val layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.griditemlayout, null)
        }

        convertView?.findViewById<TextView>(R.id.textView)?.text = item
        return convertView
    }

    fun updateItems(newItems: List<String>){
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItem(p0: Int): Any? {
        return null
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return items.size
    }

}