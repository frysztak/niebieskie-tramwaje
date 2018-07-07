package com.orpington.software.rozkladmpk.stopsAndRoutes

interface StopsAndRoutesContract {
    interface Presenter {
        fun queryTextChanged(newText: String)
        fun listItemClicked(position: Int)
    }

    interface View {
        fun navigateToRouteVariants(stopName: String)
        fun displayStops(stops: List<String>)
    }
}