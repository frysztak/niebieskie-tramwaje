package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

@Entity(tableName = "stops")
data class Stop constructor(
    @PrimaryKey
    @ColumnInfo(name = "stop_id")
    val id: Int,

    @ColumnInfo(name = "stop_code")
    val code: String,

    @ColumnInfo(name = "stop_name")
    val name: String,

    @ColumnInfo(name = "stop_lat")
    val latitude: Float,

    @ColumnInfo(name = "stop_lon")
    val longitude: Float
)


