package software.orpington.rozkladmpk.routeMap

import software.orpington.rozkladmpk.data.model.MapData
import software.orpington.rozkladmpk.data.model.VehiclePositions
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
        view?.showProgressBar()
        dataSource.getRouteMapData(routeID, stopName, direction, object : IDataSource.LoadDataCallback<MapData> {
            override fun onDataLoaded(data: MapData) {
                view?.hideProgressBar()
                view?.displayMapData(data)
            }

            override fun onDataNotAvailable() {
                view?.hideProgressBar()
                view?.reportError()
            }
        })
    }

    override fun updateVehiclePosition(routeID: String) {
        dataSource.getVehiclePosition(routeID, object : IDataSource.LoadDataCallback<VehiclePositions> {
            override fun onDataLoaded(data: VehiclePositions) {
                view?.displayVehiclePositions(data)
            }

            override fun onDataNotAvailable() {
            }
        })
    }
}