package com.orpington.software.rozkladmpk.data.source

import com.orpington.software.rozkladmpk.data.model.RouteVariants
import com.orpington.software.rozkladmpk.data.model.StopNames
import com.orpington.software.rozkladmpk.data.source.local.LocalDataSource
import com.orpington.software.rozkladmpk.data.source.remote.RemoteDataSource


class Repository(private val remoteDataSource: RemoteDataSource,
                 private val localDataSource: LocalDataSource) : RepositoryDataSource {

    companion object {

        private var INSTANCE: Repository? = null

        fun getInstance(remoteDataSource: RemoteDataSource,
                        localDataSource: LocalDataSource): Repository {
            if (INSTANCE == null) {
                INSTANCE = Repository(remoteDataSource, localDataSource)
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun getAllStopNames(callback: IDataSource.LoadDataCallback<StopNames>, isNetworkAvailable: Boolean) {
        remoteDataSource.getStopNames(object : IDataSource.LoadDataCallback<StopNames> {
            override fun onDataLoaded(data: StopNames) {
                //refreshSourceLocalDataSource(sources)
                callback.onDataLoaded(data)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getRouteVariantsForStopName(stopName: String, callback: IDataSource.LoadDataCallback<RouteVariants>, isNetworkAvailable: Boolean) {
        remoteDataSource.getRouteVariantsForStopName(stopName, object : IDataSource.LoadDataCallback<RouteVariants> {
            override fun onDataLoaded(data: RouteVariants) {
                //refreshSourceLocalDataSource(sources)
                callback.onDataLoaded(data)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }
}