package com.orpington.software.rozkladmpk.routeDetails

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orpington.software.rozkladmpk.R


class RouteDetailsPagerAdapter(private val context: Context) : PagerAdapter() {
    private val layouts = listOf(R.layout.route_directions, R.layout.route_timetable)

    override fun getCount(): Int {
        return layouts.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(layouts[position], container, false) as ViewGroup
        when (position) {
            0 -> layout.findViewById<RecyclerView>(R.id.routeDirections_recyclerview).apply {
                adapter = RouteDirectionsAdapter(context)
                layoutManager = LinearLayoutManager(context)
            }
        }
        container.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}