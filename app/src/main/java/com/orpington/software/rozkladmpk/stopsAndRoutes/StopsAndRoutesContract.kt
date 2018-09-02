package com.orpington.software.rozkladmpk.stopsAndRoutes

import com.orpington.software.rozkladmpk.BaseView
import com.orpington.software.rozkladmpk.data.model.StopsAndRoutes

interface StopsAndRoutesContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()

        fun loadStopsAndRoutes()
        fun setStopsAndRoutes(data: StopsAndRoutes)

        fun queryTextChanged(newText: String)
        fun stopClicked(stopName: String)
        fun routeClicked(routeID: String)

        fun locationChanged(latitude: Double, longitude: Double)
    }

    interface View : BaseView {
        fun navigateToRouteVariants(stopName: String)
        fun navigateToStopsForRoute(routeID: String)

        fun setStopsAndRoutes(data: List<StopOrRoute>)
        fun setSearchResults(data: List<StopOrRoute>)
        fun setNearbyStops(data: List<StopOrRoute>)

        fun showStopNotFound()
        fun showStopsList()
    }
}