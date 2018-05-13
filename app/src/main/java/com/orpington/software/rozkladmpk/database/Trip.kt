package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip (
    @ColumnInfo(name = "route_id")
    val routeId: String,

    @ColumnInfo(name = "service_id")
    val serviceId: Int,

    @ColumnInfo(name = "trip_id")
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "trip_headsign")
    val headSign: String,

    @ColumnInfo(name = "vehicle_id")
    val vehicleId: Int,

    @ColumnInfo(name = "variant_id")
    val variantId: Int
)
