package com.orpington.software.rozkladmpk.routeDetails

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class RouteDetailsPagerAdapter(
    private val presenter: RouteDetailsContract.Presenter,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                val fragment = RouteDirectionsFragment.newInstance()
                fragment.attachPresenter(presenter)
                fragment
            }
            else -> {
                val fragment = RouteTimetableFragment.newInstance()
                fragment.attachPresenter(presenter)
                fragment
            }
        }
    }

}