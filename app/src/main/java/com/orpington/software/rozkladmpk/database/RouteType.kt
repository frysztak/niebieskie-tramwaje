package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

enum class RouteTypeEnum {
    INVALID,
    NORMAL_BUS,
    NORMAL_TRAM,
    SUBURBAN_BUS,
    EXPRESS_BUS,
    ZONE_BUS,
    NIGHT_BUS
}

public fun getEnumForRouteType(routeTypeId: Int): RouteTypeEnum {
    return when (routeTypeId) {
        30 -> RouteTypeEnum.NORMAL_BUS
        31 -> RouteTypeEnum.NORMAL_TRAM
        34 -> RouteTypeEnum.SUBURBAN_BUS
        35 -> RouteTypeEnum.EXPRESS_BUS
        39 -> RouteTypeEnum.ZONE_BUS
        40 -> RouteTypeEnum.NIGHT_BUS
        else -> RouteTypeEnum.INVALID
    }
}

@Entity(tableName = "route_types")
data class RouteType constructor(
    @PrimaryKey
    @ColumnInfo(name = "route_type2_id")
    val id: Int,

    @ColumnInfo(name = "route_type2_name")
    val name: String
)