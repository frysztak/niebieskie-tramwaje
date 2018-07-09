package com.orpington.software.rozkladmpk.data.source

import android.util.Log
import com.orpington.software.rozkladmpk.data.model.RouteVariants
import com.orpington.software.rozkladmpk.data.model.StopNames
import mu.KotlinLogging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RemoteDataSource private constructor(private val service: ApiService) : IRemoteDataSource {

    override fun getStopNames(callback: IDataSource.LoadDataCallback<StopNames>) {
        val articleResponseCall = service.getStops()
        articleResponseCall.enqueue(object : Callback<StopNames> {
            override fun onResponse(call: Call<StopNames>, response: Response<StopNames>) {
                if (response.isSuccessful) {
                    val stopNames = response.body()
                    stopNames?.let {
                        logger.debug { "getStopNames: success" }
                        callback.onDataLoaded(it) }
                    ?: run {
                        logger.debug { "getStopNames: received null list. code: ${response.code()}" }
                    }
                } else {
                    logger.debug { "getStopNames: request unsuccessful. code: ${response.code()}" }
                }
            }

            override fun onFailure(call: Call<StopNames>, t: Throwable) {
                logger.debug { "getStopNames: request failed. message: ${t.message}" }
            }
        })
    }

    override fun getRouteVariantsForStopName(stopName: String, callback: IDataSource.LoadDataCallback<RouteVariants>) {
        val articleResponseCall = service.getRouteVariantsForStopName(stopName)
        articleResponseCall.enqueue(object : Callback<RouteVariants> {
            override fun onResponse(call: Call<RouteVariants>, response: Response<RouteVariants>) {
                if (response.isSuccessful) {
                    val stopNames = response.body()
                    stopNames?.let {
                        callback.onDataLoaded(it) }
                    ?: run {
                        Log.e(LOG_TAG, "Oops, something went wrong!")
                    }
                } else {
                    Log.e(LOG_TAG, "Oops, something went wrong!")
                }
            }

            override fun onFailure(call: Call<RouteVariants>, t: Throwable) {
                Log.e(LOG_TAG, "Error:" + t.message)
            }
        })
    }

    companion object {

        private val logger = KotlinLogging.logger {}

        private val LOG_TAG = RemoteDataSource::class.java.simpleName

        private var INSTANCE: RemoteDataSource? = null

        fun getInstance(service: ApiService): RemoteDataSource {
            if (INSTANCE == null) {
                INSTANCE = RemoteDataSource(service)
            }
            return INSTANCE!!
        }
    }
}
