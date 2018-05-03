package com.orpington.software.rozkladmpk

import android.content.Context
import com.orpington.software.rozkladmpk.database.AppDatabase
import com.orpington.software.rozkladmpk.database.TransportLine

class TransportLinesInteractorImpl(context: Context): TransportLinesInteractor {
    private var db: AppDatabase = AppDatabase.getDatabase(context)

    override fun getLinesStartingWith(str: String): List<TransportLine> {
        return db.transportLineDao().findByName(str)
    }

    override fun getAllStationNames(): List<String> {
        return db.stationDao().getAllStationNames()
    }

    override fun getStationsStartingWith(str: String): List<String> {
        return db.stationDao().getStationNamesStartingWith(str)
    }
}