package software.orpington.rozkladmpk.routeMap

import software.orpington.rozkladmpk.data.model.Shape

interface RouteMapContract {
    interface Presenter {
        fun attachView(v: View)
        fun detachView()

        fun loadShapes(routeID: String, direction: String, stopName: String)
    }

    interface View {
        fun displayShapes(shapes: List<Shape>)
    }
}