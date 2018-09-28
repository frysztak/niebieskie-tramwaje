package software.orpington.rozkladmpk.home.map

import software.orpington.rozkladmpk.BaseView
import software.orpington.rozkladmpk.data.model.MapData
import software.orpington.rozkladmpk.data.model.Shape
import software.orpington.rozkladmpk.data.model.StopsAndRoutes
import software.orpington.rozkladmpk.data.model.VehiclePositions

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
    val onDemand: Boolean,
    val tripID: Int,
    val isTracked: Boolean,
    val shapeColour: Int
) : DepartureViewItem()

object DepartureNotFound: DepartureViewItem()
object DepartureShowMore : DepartureViewItem()

interface MapContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()

        fun loadStops()

        fun locationChanged(latitude: Double, longitude: Double)
        fun loadDepartures(stopNames: List<String>)

        fun onShowMoreClicked(position: Int)

        fun onTrackButtonClicked(position: Int)

        fun retryToLoadData()
    }

    interface View : BaseView {
        fun showDepartures(data: List<DepartureViewItem>)
        fun showStopMarkers(stops: List<StopsAndRoutes.Stop>)

        fun drawShape(shape: Shape, colour: Int)
        fun clearShapes()

        fun drawStops(stops: List<MapData.Stop>)
        fun clearStops()

        fun drawVehicleMarkers(positions: VehiclePositions)
        fun clearVehicleMarkers()
    }
}