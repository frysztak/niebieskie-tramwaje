package com.orpington.software.rozkladmpk

import com.orpington.software.rozkladmpk.database.*

interface TransportLinesInteractor {
    fun getLinesStartingWith(str: String): List<Route>
    fun getAllStationNames(): List<String>
    fun getStopNamesStartingWith(str: String): List<String>
    fun getFullLineName(lineId: Int): String
    fun getStationName(stationId: Int): String
    fun getRouteType(routeId: String): RouteType

    fun getRouteInfoForStop(stopName: String): List<VariantStopDao.RouteInfo>
}