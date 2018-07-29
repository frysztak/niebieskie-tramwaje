package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.data.model.RouteDirections

interface RouteDirectionsContract {
    interface Presenter {
        fun loadRouteDirections(routeID: String)
        fun directionClicked(directionIdx: Int)
    }

    interface View {
        fun showRouteDirections(routeDirections: RouteDirections)
        fun showTimetable(direction: String)
    }
}