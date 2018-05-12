package com.orpington.software.rozkladmpk

import com.orpington.software.rozkladmpk.database.Stop
import com.orpington.software.rozkladmpk.database.Route
import com.orpington.software.rozkladmpk.database.RouteType

interface TransportLinesInteractor {
    fun getLinesStartingWith(str: String): List<Route>
    fun getAllStationNames(): List<String>
    fun getStopNamesStartingWith(str: String): List<String>
    fun getFullLineName(lineId: Int): String
    fun getLinesForStation(stationId: Int): List<Route>
    fun getStationName(stationId: Int): String
    fun getRouteType(routeId: String): RouteType
}