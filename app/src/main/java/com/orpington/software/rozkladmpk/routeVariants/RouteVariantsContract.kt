package com.orpington.software.rozkladmpk.routeVariants

import com.orpington.software.rozkladmpk.BasePresenter
import com.orpington.software.rozkladmpk.BaseView
import com.orpington.software.rozkladmpk.data.model.RouteVariant

interface RouteVariantsContract {
    interface Presenter: BasePresenter {
        fun loadVariants(stopName: String)
        fun routeClicked(position: Int)
    }

    interface View: BaseView {
        fun showRoutes(variants: List<RouteVariant>)
        fun showVariants(variants: List<RouteVariant>)
        fun reportThatSomethingWentWrong()
    }
}