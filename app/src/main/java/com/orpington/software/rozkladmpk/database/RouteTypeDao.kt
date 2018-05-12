package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface RouteTypeDao {
    @Insert
    fun insert(lines: List<RouteType>)

    @Query("SELECT * FROM route_types WHERE route_type2_id=:routeTypeId")
    fun getRouteType(routeTypeId: Int): RouteType
}