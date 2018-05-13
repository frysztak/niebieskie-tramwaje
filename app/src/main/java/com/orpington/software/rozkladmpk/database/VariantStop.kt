package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "variant_stops")
data class VariantStop(
    @PrimaryKey
    @ColumnInfo(name = "variant_id")
    val variantId: Int,

    @ColumnInfo(name = "first_stop_name")
    val firstStopName: String,

    @ColumnInfo(name = "last_stop_name")
    val lastStopName: String
)
