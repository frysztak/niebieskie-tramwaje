package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.data.model.RouteDirections
import com.orpington.software.rozkladmpk.data.model.RouteInfo

interface RouteDetailsContract {
    interface Presenter {
        fun attachInfoView(view: InfoView)
        fun attachDirectionsView(view: DirectionsView)

        fun setRouteID(id: String)
        fun setStopName(name: String)

        fun loadRouteInfo()

        fun loadRouteDirections()
        fun directionClicked(directionIdx: Int)
    }

    interface InfoView {
        fun showRouteInfo(routeInfo: RouteInfo)
    }

    interface DirectionsView {
        fun attachPresenter(newPresenter: RouteDetailsContract.Presenter)

        fun showRouteDirections(routeDirections: RouteDirections)
        fun showTimetable(direction: String)
    }
}