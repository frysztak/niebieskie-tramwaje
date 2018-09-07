package software.orpington.rozkladmpk.stopsAndRoutes

import software.orpington.rozkladmpk.BaseView
import software.orpington.rozkladmpk.data.model.StopsAndRoutes

interface StopsAndRoutesContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()

        fun loadStopsAndRoutes()
        fun setStopsAndRoutes(data: StopsAndRoutes)

        fun queryTextChanged(newText: String)
        fun stopClicked(stopName: String)
        fun routeClicked(routeID: String)

        fun locationChanged(latitude: Double, longitude: Double)
        fun locationGooglePlayError()

        fun shouldShowNearbyStops(): Boolean
        fun shouldShowNearbyStopsPrompt(): Boolean

        fun agreeToLocationTrackingClicked()
        fun neverAskAboutLocationTrackingClicked()
    }

    interface View : BaseView {
        fun navigateToRouteVariants(stopName: String)
        fun navigateToStopsForRoute(routeID: String)

        fun setStopsAndRoutes(data: List<StopOrRoute>)
        fun setSearchResults(data: List<StopOrRoute>)
        fun setNearbyStops(data: List<StopOrRoute>?)
        fun setNearbyStopsGooglePlayError()

        fun showStopNotFound()
        fun showStopsList()

        fun isLocationPermissionGranted(): Boolean
        fun isNeverAskForLocationSet(): Boolean
        fun setNeverAskForLocation(value: Boolean)
        fun startLocationTracking()
    }
}