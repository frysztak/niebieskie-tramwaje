package com.orpington.software.rozkladmpk

import android.content.Context
import com.orpington.software.rozkladmpk.database.*

class TransportLinesInteractorImpl(context: Context): TransportLinesInteractor {
    private var db: AppDatabase = AppDatabase.getDatabase(context)

    override fun getLinesStartingWith(str: String): List<Route> {
        return db.routeDao().findByName(str)
    }

    override fun getAllStationNames(): List<String> {
        return db.stopDao().getAllStopNames()
    }

    override fun getStopNamesStartingWith(str: String): List<String> {
        return db.stopDao().getStopNamesStartingWith(str)
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

    override fun getRouteInfoForStop(stopName: String): List<VariantStopDao.RouteInfo> {
        return db.variantStopDao().getInfoForStopName(stopName)
    }

    override fun getStationName(stationId: Int): String {
        return db.stopDao().getStopName(stationId)
    }

    override fun getRouteType(routeId: String): RouteType {
        val routeTypeId = db.routeDao().getRouteTypeId(routeId)
        return db.routeTypeDao().getRouteType(routeTypeId)
    }

}