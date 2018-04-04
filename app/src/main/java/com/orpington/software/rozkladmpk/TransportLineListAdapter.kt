package com.orpington.software.rozkladmpk

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.orpington.software.rozkladmpk.database.TransportLine

class TransportLineListAdapter(private var context: Context): BaseAdapter() {
    private var lines: List<TransportLine> = emptyList()
    private var partEnteredByUser = ""

    override fun getView(position: Int, initialConvertView: View?, parent: ViewGroup?): View {
        var convertView = initialConvertView

        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(context)
            convertView = layoutInflater.inflate(R.layout.transport_line_list_layout, parent, false)
        }

        if (lines.isNotEmpty()) {
            convertView?.findViewById<TextView>(R.id.lineName)?.text = formatLineName(lines[position].name)
        }
        return convertView!!
    }

    private fun formatLineName(plainName: String): SpannableString {
        val spannable = SpannableString(plainName)
        if (partEnteredByUser.isNotEmpty()) {
            spannable.setSpan(StyleSpan(Typeface.BOLD), 0, partEnteredByUser.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(Color.RED), 0, partEnteredByUser.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannable
    }

    fun updateItems(newLines: List<TransportLine>, newPartEnteredByUser: String) {
        lines = newLines
        partEnteredByUser = newPartEnteredByUser
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Any {
        return lines[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return lines.size
    }

}