package com.orpington.software.rozkladmpk.routeVariants

import com.orpington.software.rozkladmpk.BasePresenter
import com.orpington.software.rozkladmpk.BaseView
import com.orpington.software.rozkladmpk.data.model.RouteVariant
import com.xwray.groupie.ExpandableGroup

interface RouteVariantsContract {
    interface Presenter: BasePresenter {
        fun loadVariants(stopName: String)
    }

    interface View: BaseView {
        fun showVariants(variants: List<ExpandableGroup>)
    }
}