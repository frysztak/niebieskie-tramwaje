package com.orpington.software.rozkladmpk.routeVariants

import com.orpington.software.rozkladmpk.data.model.RouteVariant
import com.orpington.software.rozkladmpk.data.model.RouteVariants
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RepositoryDataSource

class RouteVariantsPresenter(
    private val repository: RepositoryDataSource,
    private val view: RouteVariantsContract.View
) : RouteVariantsContract.Presenter {

    private var specificRoutes: List<RouteVariant> = emptyList()

    fun onItemClicked(position: Int) {
        //view.navigateToStopActivity(stop)
    }

    override fun loadVariants(stopName: String) {
        repository.getRouteVariantsForStopName(stopName, object : IDataSource.LoadDataCallback<RouteVariants> {
            override fun onDataLoaded(data: RouteVariants) {
                specificRoutes = data.routeVariants
                val sortedDistinct = specificRoutes.distinctBy { it.routeID }.sortedWith(routeInfoComparator)
                view.showVariants(sortedDistinct)
            }

            override fun onDataNotAvailable() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }, true)
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
}

