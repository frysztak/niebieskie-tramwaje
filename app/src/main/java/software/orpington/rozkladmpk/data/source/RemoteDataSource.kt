package software.orpington.rozkladmpk.data.source

import software.orpington.rozkladmpk.data.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RemoteDataSource private constructor(private val service: ApiService) : IRemoteDataSource {

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
        makeACall(service.getStopsAndRoutes(), callback)
    }

    override fun getRouteInfo(routeID: String, callback: IDataSource.LoadDataCallback<RouteInfo>) {
        makeACall(service.getRouteInfo(routeID), callback)
    }

    override fun getRouteDirections(routeID: String, callback: IDataSource.LoadDataCallback<RouteDirections>) {
        makeACall(service.getRouteDirections(routeID), callback)
    }

    override fun getRouteDirectionsThroughStop(routeID: String, stopName: String, callback: IDataSource.LoadDataCallback<RouteDirections>) {
        makeACall(service.getRouteDirectionsThroughStop(routeID, stopName), callback)
    }

    override fun getRouteVariantsForStopName(stopName: String, callback: IDataSource.LoadDataCallback<RouteVariants>) {
        makeACall(service.getRouteVariantsForStopName(stopName), callback)
    }

    override fun getStopsForRoute(routeID: String, callback: IDataSource.LoadDataCallback<Stops>) {
        makeACall(service.getStopsForRoute(routeID), callback)
    }

    override fun getTimeTable(routeID: String, stopName: String, direction: String, callback: IDataSource.LoadDataCallback<TimeTable>) {
        makeACall(service.getTimeTable(routeID, stopName, direction), callback)
    }

    override fun getTripTimeline(tripID: Int, callback: IDataSource.LoadDataCallback<Timeline>) {
        makeACall(service.getTripTimeline(tripID), callback)
    }

    companion object {

        private var INSTANCE: RemoteDataSource? = null
        fun getInstance(service: ApiService): RemoteDataSource {
            if (INSTANCE == null) {
                INSTANCE = RemoteDataSource(service)
            }
            return INSTANCE!!
        }
    }
}
