package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.data.model.RouteInfo

interface RouteDetailsContract {
    interface Presenter {
        fun loadRouteDetails(routeID: String)
    }

    interface View {
        fun showRouteInfo(routeInfo: RouteInfo)
    }
}