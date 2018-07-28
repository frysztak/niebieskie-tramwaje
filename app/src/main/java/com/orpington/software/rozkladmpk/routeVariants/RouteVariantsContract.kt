package com.orpington.software.rozkladmpk.routeVariants

import com.orpington.software.rozkladmpk.BasePresenter
import com.orpington.software.rozkladmpk.BaseView
import com.orpington.software.rozkladmpk.data.model.RouteVariant

interface RouteVariantsContract {
    interface Presenter: BasePresenter {
        fun loadVariants(stopName: String)
        fun routeClicked(position: Int)
        fun variantClicked(position: Int)
    }

    interface View: BaseView {
        fun showProgressBar()
        fun reportThatSomethingWentWrong()

        fun showRoutes(variants: List<RouteVariant>)
        fun showVariants(variants: List<RouteVariant>)

        fun navigateToRouteDetails(routeID: String, stopName: String)
        fun navigateToTimetable(routeID: String, atStop: String, fromStop: String, toStop: String)
    }
}