package software.orpington.rozkladmpk.home.map

interface MapContract {
    interface Presenter {
        fun locationChanged(latitude: Double, longitude: Double)
        fun locationGooglePlayError()
        fun setLocationIsDisabled(isDisabled: Boolean)

        fun shouldShowNearbyStops(): Boolean
        fun shouldShowNearbyStopsPrompt(): Boolean

        fun agreeToLocationTrackingClicked()
        fun enableLocationClicked()
    }

    interface View {
        fun isLocationPermissionGranted(): Boolean
        fun startLocationTracking()
        fun showLocationSettings()
    }
}