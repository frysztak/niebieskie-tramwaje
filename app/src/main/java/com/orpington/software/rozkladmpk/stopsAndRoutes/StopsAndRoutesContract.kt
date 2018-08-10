package com.orpington.software.rozkladmpk.stopsAndRoutes

import com.orpington.software.rozkladmpk.BaseView
import com.orpington.software.rozkladmpk.data.model.StopsAndRoutes

interface StopsAndRoutesContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()

        fun loadStopsAndRoutes()
        fun setStopsAndRoutes(data: StopsAndRoutes)
        fun setShownStopsAndRoutes(data: StopsAndRoutes)

        fun queryTextChanged(newText: String)
        fun listItemClicked(position: Int)
        fun stopClicked(stopName: String)
        fun routeClicked(routeID: String)
    }

    interface View : BaseView {
        fun navigateToRouteVariants(stopName: String)
        fun displayStopsAndRoutes(data: StopsAndRoutes)
        fun showStopNotFound()

        // delegate this to adapter
        fun listItemClicked(position: Int)
    }
}