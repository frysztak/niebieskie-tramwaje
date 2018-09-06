package software.orpington.rozkladmpk.routeVariants

import software.orpington.rozkladmpk.BasePresenter
import software.orpington.rozkladmpk.BaseView
import software.orpington.rozkladmpk.data.model.RouteVariant

interface RouteVariantsContract {
    interface Presenter: BasePresenter {
        fun attachView(view: View)
        fun detachView()

        fun loadVariants(stopName: String)
        fun routeClicked(position: Int)
    }

    interface View: BaseView {
        fun showRoutes(variants: List<RouteVariant>)
        fun navigateToRouteDetails(routeID: String, stopName: String)
    }
}