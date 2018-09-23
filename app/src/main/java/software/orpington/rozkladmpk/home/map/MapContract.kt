package software.orpington.rozkladmpk.home.map

import software.orpington.rozkladmpk.BaseView

sealed class DepartureViewItem
data class DepartureHeader(
    val stopName: String,
    val stopID: Int,
    val distance: Float
) : DepartureViewItem()

data class DepartureDetails(
    val isBus: Boolean,
    val routeID: String,
    val direction: String,
    val departureInMinutes: Int,
    val departureTime: String,
    val onDemand: Boolean
) : DepartureViewItem()

object DepartureShowMore : DepartureViewItem()

interface MapContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()

        fun loadStops()

        fun locationChanged(latitude: Double, longitude: Double)
        fun loadDepartures(stopNames: List<String>)

        fun onShowMoreClicked(position: Int)
    }

    interface View : BaseView {
        fun showDepartures(data: List<DepartureViewItem>)
    }
}