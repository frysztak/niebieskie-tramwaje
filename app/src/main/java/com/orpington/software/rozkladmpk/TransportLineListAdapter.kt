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
import com.orpington.software.rozkladmpk.database.TransportType

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
            var line = lines[position]
            convertView?.findViewById<TextView>(R.id.icon)?.text = getIcon(line)
            convertView?.findViewById<TextView>(R.id.lineName)?.text = formatLineName(line.name)
            convertView?.findViewById<TextView>(R.id.stops)?.text = getFirstAndLastStop(line)
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

    private fun getFirstAndLastStop(line: TransportLine): String {
        return "${line.stops.first()} —${line.stops.last()}"
    }

    private fun getIcon(line: TransportLine): String {
        return if (line.type == TransportType.BUS) "\uD83D\uDE8C" else "\uD83D\uDE8B"
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