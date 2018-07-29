package com.orpington.software.rozkladmpk.data.source

import com.orpington.software.rozkladmpk.data.model.*
import com.orpington.software.rozkladmpk.data.source.IDataSource.LoadDataCallback


interface IRemoteDataSource : IDataSource {
    fun getStopNames(callback: LoadDataCallback<StopNames>)

    fun getRouteInfo(routeID: String, callback: LoadDataCallback<RouteInfo>)
    fun getRouteDirections(routeID: String, callback: LoadDataCallback<RouteDirections>)

    fun getRouteVariantsForStopName(stopName: String, callback: LoadDataCallback<RouteVariants>)

    fun getTimeTable(routeID: String, atStop: String, fromStop: String, toStop: String, callback: LoadDataCallback<TimeTable>)
}