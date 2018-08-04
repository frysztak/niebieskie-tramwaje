package com.orpington.software.rozkladmpk.stopsAndRoutes

interface StopsAndRoutesContract {
    interface Presenter {
        fun attachView(view: View)

        fun loadStopNames()
        fun setAllStopNames(names: List<String>)
        fun setShownStopNames(names: List<String>)

        fun queryTextChanged(newText: String)
        fun listItemClicked(position: Int)
    }

    interface View {
        fun showProgressBar()
        fun hideProgressBar()
        fun navigateToRouteVariants(stopName: String)
        fun displayStops(stops: List<String>)
        fun showStopNotFound()
        fun reportThatSomethingWentWrong()
    }
}