package com.orpington.software.rozkladmpk

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

class TransportLineListAdapter(
   private var context: Context,
   private var presenter: TransportLinesPresenter,
   private var clickListener: RecyclerViewClickListener
): RecyclerView.Adapter<ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
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

    /*
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
    */

}