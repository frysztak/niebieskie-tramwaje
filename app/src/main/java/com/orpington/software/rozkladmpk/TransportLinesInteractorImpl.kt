package com.orpington.software.rozkladmpk

import android.content.Context
import com.orpington.software.rozkladmpk.database.AppDatabase
import com.orpington.software.rozkladmpk.database.Station
import com.orpington.software.rozkladmpk.database.TransportLine

class TransportLinesInteractorImpl(context: Context): TransportLinesInteractor {
    private var db: AppDatabase = AppDatabase.getDatabase(context)

    override fun getLinesStartingWith(str: String): List<TransportLine> {
        return db.transportLineDao().findByName(str)
    }

    override fun getAllStationNames(): List<String> {
        return db.stationDao().getAllStationNames()
    }

    override fun getStationsStartingWith(str: String): List<Station> {
        return db.stationDao().getStationNamesStartingWith(str)
    }

    override fun getFullLineName(lineId: Int): String {
        var stationNames = db.lineStationJoinDao().getFirstAndLastStationName(lineId)
        var lineName = db.transportLineDao().getName(lineId)
        var arrow = "->"
        return "$lineName: ${stationNames.first()} $arrow ${stationNames.last()}"
    }

    override fun getLinesForStation(stationId: Int): List<TransportLine> {
        return db.lineStationJoinDao().getLinesForStation(stationId)
    }

    override fun getStationName(stationId: Int): String {
        return db.stationDao().getStationName(stationId)
    }

}