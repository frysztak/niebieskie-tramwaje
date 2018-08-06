package com.orpington.software.rozkladmpk.routeDetails

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.orpington.software.rozkladmpk.R

class RouteDetailsPagerAdapter(
    private val context: Context,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    private val pageTitles = listOf(R.string.direction, R.string.timetable, R.string.timeline)

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.getString(pageTitles[position])
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> RouteDirectionsFragment.newInstance()
            1 -> RouteTimetableFragment.newInstance()
            2 -> RouteTimelineFragment.newInstance()
            else -> null
        }

    }

}