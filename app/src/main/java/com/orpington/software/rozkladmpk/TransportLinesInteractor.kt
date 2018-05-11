package com.orpington.software.rozkladmpk

import com.orpington.software.rozkladmpk.database.Station
import com.orpington.software.rozkladmpk.database.TransportLine

interface TransportLinesInteractor {
    fun getLinesStartingWith(str: String): List<TransportLine>
    fun getAllStationNames(): List<String>
    fun getStationsStartingWith(str: String): List<Station>
    fun getFullLineName(lineId: Int): String
    fun getLinesForStation(stationId: Int): List<TransportLine>
    fun getStationName(stationId: Int): String
}