package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.BaseView
import com.orpington.software.rozkladmpk.data.model.RouteInfo

interface RouteDetailsContract {
    interface Presenter {
        fun attachView(view: View)
        fun dropView()

        fun loadRouteInfo()
    }

    interface View : BaseView, RouteDirectionsEventListener {
        fun showRouteInfo(routeInfo: RouteInfo)
        fun switchToTimetableTab()
        fun switchToTimelineTab()
    }
}