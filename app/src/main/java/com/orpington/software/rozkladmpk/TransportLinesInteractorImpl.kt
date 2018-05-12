package com.orpington.software.rozkladmpk

import android.content.Context
import com.orpington.software.rozkladmpk.database.AppDatabase
import com.orpington.software.rozkladmpk.database.Route
import com.orpington.software.rozkladmpk.database.RouteType

class TransportLinesInteractorImpl(context: Context): TransportLinesInteractor {
    private var db: AppDatabase = AppDatabase.getDatabase(context)

    override fun getLinesStartingWith(str: String): List<Route> {
        return db.routeDao().findByName(str)
    }

    override fun getAllStationNames(): List<String> {
        return db.stationDao().getAllStopNames()
    }

    override fun getStopNamesStartingWith(str: String): List<String> {
        return db.stationDao().getStopNamesStartingWith(str)
    }

    override fun getFullLineName(lineId: Int): String {
        /*
        var stationNames = db.lineStationJoinDao().getFirstAndLastStationName(lineId)
        var lineName = db.routeDao().getName(lineId)
        var arrow = "->"
        return "$lineName: ${stationNames.first()} $arrow ${stationNames.last()}"
        */
        return "dummy"
    }

    override fun getLinesForStation(stationId: Int): List<Route> {
        //return db.lineStationJoinDao().getLinesForStation(stationId)
        return emptyList()
    }

    override fun getStationName(stationId: Int): String {
        return db.stationDao().getStopName(stationId)
    }

    override fun getRouteType(routeId: String): RouteType {
        val routeTypeId = db.routeDao().getRouteTypeId(routeId)
        return db.routeTypeDao().getRouteType(routeTypeId)
    }

}