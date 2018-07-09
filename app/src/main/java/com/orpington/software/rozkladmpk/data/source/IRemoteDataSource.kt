package com.orpington.software.rozkladmpk.data.source

import com.orpington.software.rozkladmpk.data.model.RouteVariants
import com.orpington.software.rozkladmpk.data.model.StopNames
import com.orpington.software.rozkladmpk.data.source.IDataSource.LoadDataCallback


interface IRemoteDataSource : IDataSource {
    fun getStopNames(callback: LoadDataCallback<StopNames>)

    fun getRouteVariantsForStopName(stopName: String, callback: LoadDataCallback<RouteVariants>)
}