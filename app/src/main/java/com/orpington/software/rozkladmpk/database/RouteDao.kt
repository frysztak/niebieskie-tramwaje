package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface RouteDao {
    @Insert
    fun insert(lines: List<Route>)

    @Query("SELECT * FROM routes")
    fun getAll(): List<Route>

    @Query("SELECT * FROM routes WHERE route_id LIKE :name || '%' ORDER BY route_id")
    fun findByName(name: String): List<Route>

    @Query("SELECT route_type2_id FROM ROUTES WHERE route_id=:routeId")
    fun getRouteTypeId(routeId: String): Int
}