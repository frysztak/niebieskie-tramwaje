package software.orpington.rozkladmpk.routeMap

import software.orpington.rozkladmpk.data.model.MapData
import software.orpington.rozkladmpk.data.model.VehiclePositions

interface RouteMapContract {
    interface Presenter {
        fun attachView(v: View)
        fun detachView()

        fun loadShapes(routeID: String, direction: String, stopName: String)
        fun updateVehiclePosition(routeID: String)
    }

    interface View {
        fun showProgressBar()
        fun hideProgressBar()
        fun reportError()

        fun displayMapData(mapData: MapData)
        fun displayVehiclePositions(data: VehiclePositions)
    }
}