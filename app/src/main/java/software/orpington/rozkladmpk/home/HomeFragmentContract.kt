package software.orpington.rozkladmpk.home

import software.orpington.rozkladmpk.BaseView

interface HomeFragmentContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()

        fun loadData()
        fun queryTextChanged(newText: String)
        fun stopClicked(stopName: String)
        fun routeClicked(routeID: String)
    }

    interface View: BaseView {
        fun navigateToRouteVariants(stopName: String)
        fun navigateToStopsForRoute(routeID: String)

        fun showStopNotFound()
        fun showSearchResults(data: List<StopOrRoute>)
        fun hideSearchResults()
    }
}