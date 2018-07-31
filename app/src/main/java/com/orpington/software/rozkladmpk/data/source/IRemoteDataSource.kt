package com.orpington.software.rozkladmpk.data.source

import com.orpington.software.rozkladmpk.data.model.*
import com.orpington.software.rozkladmpk.data.source.IDataSource.LoadDataCallback


interface IRemoteDataSource : IDataSource {
    fun getStopNames(callback: LoadDataCallback<StopNames>)

    fun getRouteInfo(routeID: String, callback: LoadDataCallback<RouteInfo>)
    fun getRouteDirections(routeID: String, callback: LoadDataCallback<RouteDirections>)
    fun getRouteDirectionsThroughStop(routeID: String, stopName: String, callback: LoadDataCallback<RouteDirections>)

    fun getRouteVariantsForStopName(stopName: String, callback: LoadDataCallback<RouteVariants>)

    fun getTimeTable(routeID: String, stopName: String, direction: String, callback: LoadDataCallback<TimeTable>)
}