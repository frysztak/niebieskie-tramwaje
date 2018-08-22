package com.orpington.software.rozkladmpk.stopsAndRoutes

import com.orpington.software.rozkladmpk.data.model.StopsAndRoutes
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import com.orpington.software.rozkladmpk.utils.GeoLocation


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

    // used to show nearby stops
    private var rawStops: List<StopsAndRoutes.Stop> = emptyList()
    private var stopsAndRoutes: List<StopOrRoute> = emptyList()
    override fun setStopsAndRoutes(data: StopsAndRoutes) {
        val helper = StopsAndRoutesHelper()
        stopsAndRoutes = helper.convertModel(data)
        rawStops = data.stops
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
        if (newText.isEmpty()) {
            view?.displaySearchResults(emptyList())
            return
        }

        val helper = StopsAndRoutesHelper()
        val searchResults = helper.filterItems(stopsAndRoutes, newText)

        if (searchResults.isNotEmpty()) {
            view?.displaySearchResults(searchResults)
        } else {
            view?.showStopNotFound()
        }
    }

    override fun stopClicked(stopName: String) {
        view?.navigateToRouteVariants(stopName)
    }

    override fun routeClicked(routeID: String) {
        view?.navigateToStopsForRoute(routeID)
    }

    override fun locationChanged(latitude: Double, longitude: Double) {
        val location = GeoLocation.fromDegrees(latitude, longitude)
        val helper = StopsAndRoutesHelper()
        val nearbyStops = helper.filterNearbyStops(rawStops, location)
        view?.displayNearbyStops(nearbyStops)
    }

}

