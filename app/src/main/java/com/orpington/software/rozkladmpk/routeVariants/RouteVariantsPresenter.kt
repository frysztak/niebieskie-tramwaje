package com.orpington.software.rozkladmpk.routeVariants

import com.orpington.software.rozkladmpk.data.model.RouteVariant
import com.orpington.software.rozkladmpk.data.model.RouteVariants
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource

class RouteVariantsPresenter(
    private val dataSource: RemoteDataSource,
    private val view: RouteVariantsContract.View
) : RouteVariantsContract.Presenter {

    private var routes: List<RouteVariant> = emptyList()
    private var sortedDistinctRoutes: List<RouteVariant> = emptyList()

    override fun loadVariants(stopName: String) {
        dataSource.getRouteVariantsForStopName(stopName, object : IDataSource.LoadDataCallback<RouteVariants> {
            override fun onDataLoaded(data: RouteVariants) {
                routes = data.routeVariants
                sortedDistinctRoutes = routes.distinctBy { it.routeID }.sortedWith(routeInfoComparator)
                view.showRoutes(sortedDistinctRoutes)
            }

            override fun onDataNotAvailable() {
                view.reportThatSomethingWentWrong()
            }

        })
    }

    private val routeInfoComparator = Comparator<RouteVariant> { p0, p1 ->
        var intId0 = p0.routeID.toIntOrNull()
        var intId1 = p1.routeID.toIntOrNull()
        if (intId0 != null && intId1 != null) {
            intId0.compareTo(intId1)
        } else {
            p0.routeID.compareTo(p1.routeID)
        }
    }

    override fun routeClicked(position: Int) {
        val routeID = sortedDistinctRoutes[position].routeID
        val variants = routes.filter { it.routeID == routeID }
        view.showVariants(variants)
    }
}

