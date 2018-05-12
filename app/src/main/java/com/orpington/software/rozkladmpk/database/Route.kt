package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.*

@Entity(tableName = "routes")
data class Route constructor(
    @PrimaryKey
    @ColumnInfo(name = "route_id")
    val id: String,

    @ColumnInfo(name = "agency_id")
    val agencyId: Int,

    @ColumnInfo(name = "route_desc")
    val description: String,

    @ColumnInfo(name = "route_type2_id")
    val type: Int,

    @ColumnInfo(name = "valid_from")
    val validFrom: String,

    @ColumnInfo(name = "valid_until")
    val validUntil: String
)
