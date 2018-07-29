package com.orpington.software.rozkladmpk.data.source

import com.orpington.software.rozkladmpk.data.model.*
import mu.KotlinLogging
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
                        logger.debug { "${call.request().url()}: success" }
                        callback.onDataLoaded(it)
                    } ?: run {
                        logger.debug { "${call.request().url()}: received null list. code: ${response.code()}" }
                        callback.onDataNotAvailable()
                    }
                } else {
                    logger.debug { "${call.request().url()}: request unsuccessful. code: ${response.code()}" }
                    callback.onDataNotAvailable()
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                logger.debug { "${call.request().url()}: request failed. message: ${t.message}" }
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getStopNames(callback: IDataSource.LoadDataCallback<StopNames>) {
        makeACall(service.getStops(), callback)
    }

    override fun getRouteInfo(routeID: String, callback: IDataSource.LoadDataCallback<RouteInfo>) {
        makeACall(service.getRouteInfo(routeID), callback)
    }

    override fun getRouteDirections(routeID: String, callback: IDataSource.LoadDataCallback<RouteDirections>) {
        makeACall(service.getRouteDirections(routeID), callback)
    }

    override fun getRouteVariantsForStopName(stopName: String, callback: IDataSource.LoadDataCallback<RouteVariants>) {
        makeACall(service.getRouteVariantsForStopName(stopName), callback)
    }

    override fun getTimeTable(routeID: String, atStop: String, fromStop: String, toStop: String, callback: IDataSource.LoadDataCallback<TimeTable>) {
        makeACall(service.getTimeTable(routeID, atStop, fromStop, toStop), callback)
    }

    companion object {

        private val logger = KotlinLogging.logger {}

        private var INSTANCE: RemoteDataSource? = null
        fun getInstance(service: ApiService): RemoteDataSource {
            if (INSTANCE == null) {
                INSTANCE = RemoteDataSource(service)
            }
            return INSTANCE!!
        }
    }
}
