package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey

@Entity(tableName = "line_station_join",
    primaryKeys = arrayOf("lineId", "stationId"),
    foreignKeys = arrayOf(
        ForeignKey(entity = TransportLine::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("lineId")),
        ForeignKey(entity = Station::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("stationId"))
    )
)
data class LineStationJoin(
    val lineId: Int,
    val stationId: Int,
    val ordinalNumber: Int
)