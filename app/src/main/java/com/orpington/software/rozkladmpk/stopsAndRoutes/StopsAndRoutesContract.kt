package com.orpington.software.rozkladmpk.stopsAndRoutes

import com.orpington.software.rozkladmpk.BaseView
import com.orpington.software.rozkladmpk.data.model.StopsAndRoutes

interface StopsAndRoutesContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()

        fun loadStopsAndRoutes()
        fun setStopsAndRoutes(data: StopsAndRoutes)
        fun setShownStopsAndRoutes(data: List<StopOrRoute>)

        fun queryTextChanged(newText: String)
        fun listItemClicked(position: Int)
    }

    interface View : BaseView {
        fun navigateToRouteVariants(stopName: String)
        fun navigateToStopsForRoute(routeID: String)

        fun displayStopsAndRoutes(data: List<StopOrRoute>)
        fun showStopNotFound()
    }
}