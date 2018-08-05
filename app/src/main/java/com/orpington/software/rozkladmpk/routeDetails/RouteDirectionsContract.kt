package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.BaseView
import com.orpington.software.rozkladmpk.data.model.RouteDirections

interface RouteDirectionsContract {
    interface Presenter {
        fun attachView(view: View)
        fun dropView()

        fun loadRouteDirections()
        fun onDirectionClicked(directionIdx: Int)
    }

    interface View : BaseView, RouteDirectionsEventListener {
        fun showRouteDirections(routeDirections: RouteDirections,
                                directionIdxToHighlight: Int = -1)

        fun highlightDirection(directionIdx: Int)
        fun unhighlightDirection(directionIdx: Int)
    }
}