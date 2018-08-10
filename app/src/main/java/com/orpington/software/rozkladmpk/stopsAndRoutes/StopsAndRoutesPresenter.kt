package com.orpington.software.rozkladmpk.stopsAndRoutes

import com.orpington.software.rozkladmpk.data.model.StopsAndRoutes
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource


class StopsAndRoutesPresenter(
    private var dataSource: RemoteDataSource
) : StopsAndRoutesContract.Presenter {

    private var view: StopsAndRoutesContract.View? = null
    override fun attachView(view: StopsAndRoutesContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    private var stopsAndRoutes = StopsAndRoutes(emptyList(), emptyList())
    override fun setStopsAndRoutes(data: StopsAndRoutes) {
        stopsAndRoutes = data
    }

    private var shownStopsAndRoutes = StopsAndRoutes(emptyList(), emptyList())
    override fun setShownStopsAndRoutes(data: StopsAndRoutes) {
        shownStopsAndRoutes = data
    }

    override fun loadStopsAndRoutes() {
        //if (allStops.isNotEmpty()) return
        view?.showProgressBar()
        dataSource.getStopsAndRoutes(object : IDataSource.LoadDataCallback<StopsAndRoutes> {
            override fun onDataLoaded(data: StopsAndRoutes) {
                setStopsAndRoutes(data)
                view?.hideProgressBar()
                view?.displayStopsAndRoutes(stopsAndRoutes)
            }

            override fun onDataNotAvailable() {
                view?.hideProgressBar()
                view?.reportThatSomethingWentWrong()
            }
        })
    }

    override fun queryTextChanged(newText: String) {

        val stops = stopsAndRoutes.stops.filter { stop ->
            stop.startsWith(newText, true)
        }

        val routes = stopsAndRoutes.routes.filter { route ->
            route.routeID.startsWith(newText, true)
        }.sortedWith(Comparator { r1, r2 ->
            val id1 = r1.routeID.toIntOrNull()
            val id2 = r2.routeID.toIntOrNull()

            if (id1 != null && id2 != null) {
                id1 - id2
            } else {
                r1.routeID.compareTo(r2.routeID)
            }
        })

        val success = stops.isNotEmpty() || routes.isNotEmpty()
        shownStopsAndRoutes = StopsAndRoutes(stops, routes)

        if (success) {
            view?.displayStopsAndRoutes(shownStopsAndRoutes)
        } else {
            view?.showStopNotFound()
        }
    }

    override fun listItemClicked(position: Int) {
        view?.listItemClicked(position)
    }

    override fun stopClicked(stopName: String) {
        view?.navigateToRouteVariants(stopName)
    }

    override fun routeClicked(routeID: String) {

    }


}

