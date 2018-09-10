package software.orpington.rozkladmpk.routeMap

import software.orpington.rozkladmpk.data.model.MapData

interface RouteMapContract {
    interface Presenter {
        fun attachView(v: View)
        fun detachView()

        fun loadShapes(routeID: String, direction: String, stopName: String)
    }

    interface View {
        fun showProgressBar()
        fun hideProgressBar()
        fun reportError()

        fun displayMapData(mapData: MapData)
    }
}