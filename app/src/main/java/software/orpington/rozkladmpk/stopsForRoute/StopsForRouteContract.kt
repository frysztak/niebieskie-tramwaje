package software.orpington.rozkladmpk.stopsForRoute

import software.orpington.rozkladmpk.BaseView

interface StopsForRouteContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()

        fun loadStops(routeID: String)
        fun stopClicked(position: Int)
    }

    interface View : BaseView {
        fun showStops(stopNames: List<String>)
        fun navigateToRouteDetails(routeID: String, stopName: String)
    }
}