package software.orpington.rozkladmpk.stopsAndRoutes

import software.orpington.rozkladmpk.data.model.StopsAndRoutes
import software.orpington.rozkladmpk.data.source.IDataSource
import software.orpington.rozkladmpk.data.source.RemoteDataSource
import software.orpington.rozkladmpk.utils.GeoLocation


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
        view?.showProgressBar()
        dataSource.getStopsAndRoutes(object : IDataSource.LoadDataCallback<StopsAndRoutes> {
            override fun onDataLoaded(data: StopsAndRoutes) {
                setStopsAndRoutes(data)
                view?.hideProgressBar()
                view?.setStopsAndRoutes(stopsAndRoutes)
            }

            override fun onDataNotAvailable() {
                view?.hideProgressBar()
                view?.reportThatSomethingWentWrong()
            }
        })
    }

    override fun queryTextChanged(newText: String) {
        if (newText.isEmpty()) {
            view?.setSearchResults(emptyList())
            view?.showStopsList()
            return
        }

        val helper = StopsAndRoutesHelper()
        val searchResults = helper.filterItems(stopsAndRoutes, newText)

        if (searchResults.isNotEmpty()) {
            view?.setSearchResults(searchResults)
            view?.showStopsList()
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

        if (rawStops.isNotEmpty() && nearbyStops.isEmpty()) {
            // no nearby stops found
            view?.setNearbyStops(null)
        } else {
            view?.setNearbyStops(nearbyStops)
        }
    }

    override fun locationGooglePlayError() {
        view?.setNearbyStopsGooglePlayError()
    }

    override fun setLocationIsDisabled(isDisabled: Boolean) {
        view?.setLocationIsDisabled(isDisabled)
    }

    override fun shouldShowNearbyStops(): Boolean {
        return view?.isNeverAskForLocationSet() != true
    }

    override fun shouldShowNearbyStopsPrompt(): Boolean {
        val v = view ?: return false
        return !v.isNeverAskForLocationSet() && !v.isLocationPermissionGranted()
    }

    override fun agreeToLocationTrackingClicked() {
        view?.startLocationTracking()
    }

    override fun neverAskAboutLocationTrackingClicked() {
        view?.setNeverAskForLocation(true)
        view?.setNearbyStops(emptyList())
    }

    override fun enableLocationClicked() {
        view?.showLocationSettings()
    }

}

