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

class GridViewKeyboardAdapter(c: Context, i: Array<String>) : BaseAdapter() {
    private var mContext: Context = c
    private var items: Array<String> = i

    override fun getView(position: Int, initialConvertView: View?, parent: ViewGroup?): View? {
        var convertView = initialConvertView

        if (initialConvertView == null) {
            val layoutInflater = LayoutInflater.from(mContext)
            convertView = layoutInflater.inflate(R.layout.griditemlayout, parent, false)
        }

        convertView?.findViewById<TextView>(R.id.textView)?.text = items[position]
        return convertView
    }

    override fun getItem(p0: Int): Any? {
        return items[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return items.size
    }

}