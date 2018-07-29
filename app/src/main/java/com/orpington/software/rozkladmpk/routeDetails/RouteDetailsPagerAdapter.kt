package com.orpington.software.rozkladmpk.routeDetails

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class RouteDetailsPagerAdapter(
    private val routeID: String,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 1
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> RouteDirectionsFragment.newInstance(routeID)
            else -> RouteDirectionsFragment.newInstance(routeID)
        }
    }

}