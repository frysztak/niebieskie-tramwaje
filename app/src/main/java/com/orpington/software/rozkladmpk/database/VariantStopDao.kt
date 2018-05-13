package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query

@Dao
interface VariantStopDao {

    data class RouteInfo(
        @ColumnInfo(name = "route_id")
        val id: String,
        @ColumnInfo(name = "first_stop_name")
        val firstStopName: String,
        @ColumnInfo(name = "last_stop_name")
        val lastStopName: String,
        @ColumnInfo(name = "variant_id")
        var variantIds: List<Int>
    )


    @Query("""
       SELECT DISTINCT route_id, first_stop_name, last_stop_name, trips.variant_id from trips
       INNER JOIN variant_stops v ON v.variant_id = trips.variant_id AND v.variant_id IN (
        SELECT distinct variant_id FROM trips WHERE trip_id IN (
         SELECT DISTINCT trip_id FROM stop_times WHERE stop_id IN (
			SELECT DISTINCT stop_id FROM stops WHERE stop_name=:stopName
		 )
	    )
       )""")
    fun getInfoForStopName(stopName: String): List<RouteInfo>
}