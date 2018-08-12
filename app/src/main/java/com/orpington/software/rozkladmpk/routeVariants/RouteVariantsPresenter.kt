package com.orpington.software.rozkladmpk.routeVariants

import com.orpington.software.rozkladmpk.data.model.RouteVariant
import com.orpington.software.rozkladmpk.data.model.RouteVariants
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import com.orpington.software.rozkladmpk.utils.sort

class RouteVariantsPresenter(
    private val dataSource: RemoteDataSource
) : RouteVariantsContract.Presenter {

    private var currentStopName: String = ""
    private var routes: List<RouteVariant> = emptyList()
    private var sortedDistinctRoutes: List<RouteVariant> = emptyList()
    private var shownVariants: List<RouteVariant> = emptyList()

    override fun loadVariants(stopName: String) {
        currentStopName = stopName
        view?.showProgressBar()
        dataSource.getRouteVariantsForStopName(stopName, object : IDataSource.LoadDataCallback<RouteVariants> {
            override fun onDataLoaded(data: RouteVariants) {
                routes = data.routeVariants
                sortedDistinctRoutes = routes.distinctBy { it.routeID }.sort()
                view?.hideProgressBar()
                view?.showRoutes(sortedDistinctRoutes)
            }

            override fun onDataNotAvailable() {
                view?.hideProgressBar()
                view?.reportThatSomethingWentWrong()
            }

        })
    }

    private var view : RouteVariantsContract.View? = null
    override fun attachView(view: RouteVariantsContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun routeClicked(position: Int) {
        val routeID = sortedDistinctRoutes[position].routeID
        shownVariants = routes
            .filter { route ->
                route.routeID == routeID
            }.sortedByDescending { route ->
                route.tripIDs.size
            }
        view?.navigateToRouteDetails(routeID, currentStopName)
    }

}

