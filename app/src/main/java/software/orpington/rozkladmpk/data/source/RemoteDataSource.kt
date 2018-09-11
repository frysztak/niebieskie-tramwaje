package software.orpington.rozkladmpk.data.source

import software.orpington.rozkladmpk.data.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RemoteDataSource private constructor(
    private val mpkService: ApiService,
    private val gpsService: GPSService
) : IRemoteDataSource {

    private fun <T> makeACall(call: Call<T>, callback: IDataSource.LoadDataCallback<T>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val stopNames = response.body()
                    stopNames?.let {
                        callback.onDataLoaded(it)
                    } ?: run {
                        callback.onDataNotAvailable()
                    }
                } else {
                    callback.onDataNotAvailable()
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getStopsAndRoutes(callback: IDataSource.LoadDataCallback<StopsAndRoutes>) {
        makeACall(mpkService.getStopsAndRoutes(), callback)
    }

    override fun getRouteInfo(routeID: String, callback: IDataSource.LoadDataCallback<RouteInfo>) {
        makeACall(mpkService.getRouteInfo(routeID), callback)
    }

    override fun getRouteDirections(routeID: String, callback: IDataSource.LoadDataCallback<RouteDirections>) {
        makeACall(mpkService.getRouteDirections(routeID), callback)
    }

    override fun getRouteDirectionsThroughStop(routeID: String, stopName: String, callback: IDataSource.LoadDataCallback<RouteDirections>) {
        makeACall(mpkService.getRouteDirectionsThroughStop(routeID, stopName), callback)
    }

    override fun getRouteVariantsForStopName(stopName: String, callback: IDataSource.LoadDataCallback<RouteVariants>) {
        makeACall(mpkService.getRouteVariantsForStopName(stopName), callback)
    }

    override fun getStopsForRoute(routeID: String, callback: IDataSource.LoadDataCallback<Stops>) {
        makeACall(mpkService.getStopsForRoute(routeID), callback)
    }

    override fun getTimeTable(routeID: String, stopName: String, direction: String, callback: IDataSource.LoadDataCallback<TimeTable>) {
        makeACall(mpkService.getTimeTable(routeID, stopName, direction), callback)
    }

    override fun getTripTimeline(tripID: Int, callback: IDataSource.LoadDataCallback<Timeline>) {
        makeACall(mpkService.getTripTimeline(tripID), callback)
    }

    override fun getRouteMapData(routeID: String, stopName: String, direction: String, callback: IDataSource.LoadDataCallback<MapData>) {
        makeACall(mpkService.getRouteMap(routeID, stopName, direction), callback)
    }

    override fun getVehiclePosition(routeID: String, callback: IDataSource.LoadDataCallback<VehiclePositions>) {
        makeACall(gpsService.getVehiclePosition(routeID), callback)
    }

    companion object {

        private var INSTANCE: RemoteDataSource? = null
        fun getInstance(service: ApiService, gpsService: GPSService): RemoteDataSource {
            if (INSTANCE == null) {
                INSTANCE = RemoteDataSource(service, gpsService)
            }
            return INSTANCE!!
        }
    }
}
