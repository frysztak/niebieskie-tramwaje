package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "stop_times")
data class StopTime constructor(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "trip_id")
    val tripId: Int,

    @ColumnInfo(name = "arrival_time")
    val arrivalTime: Int,

    @ColumnInfo(name = "departure_time")
    val departureTime: Int,

    @ColumnInfo(name = "stop_id")
    val stopId: Int,

    @ColumnInfo(name = "stop_sequence")
    val stopSequence: Int
)