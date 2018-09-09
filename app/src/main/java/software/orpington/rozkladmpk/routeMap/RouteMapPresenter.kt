package software.orpington.rozkladmpk.routeMap

import software.orpington.rozkladmpk.data.model.MapData
import software.orpington.rozkladmpk.data.source.IDataSource
import software.orpington.rozkladmpk.data.source.RemoteDataSource

class RouteMapPresenter(
    private val dataSource: RemoteDataSource
) : RouteMapContract.Presenter {
    private var view: RouteMapContract.View? = null
    override fun attachView(v: RouteMapContract.View) {
        view = v
    }

    override fun detachView() {
        view = null
    }

    override fun loadShapes(routeID: String, direction: String, stopName: String) {
        dataSource.getRouteMapData(routeID, stopName, direction, object : IDataSource.LoadDataCallback<MapData> {
            override fun onDataLoaded(data: MapData) {
                processMapData(data)
            }

            override fun onDataNotAvailable() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun processMapData(mapData: MapData) {
        view?.displayMapData(mapData)
    }
}