package com.orpington.software.rozkladmpk.routeDetails

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class RouteDetailsPagerAdapter(
    private val presenter: RouteDetailsContract.Presenter,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 1
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            else -> {
                val fragment = RouteDirectionsFragment.newInstance()
                fragment.attachPresenter(presenter)
                presenter.attachDirectionsView(fragment)
                fragment
            }
            /*
            else -> {
                val fragment = RouteTimetableFragment.newInstance()
                fragment.setPresenter(timetablePresenter)
                timetablePresenter.setView(fragment)
                fragment
            }*/
        }
    }

}