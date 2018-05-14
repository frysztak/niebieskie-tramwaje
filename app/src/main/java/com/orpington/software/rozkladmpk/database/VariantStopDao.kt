package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query

@Dao
interface VariantStopDao {

    data class RouteInfo(
        @ColumnInfo(name = "route_id")
        val id: String,
        @ColumnInfo(name = "route_type2_id")
        val typeId: Int,
        @ColumnInfo(name = "route_type2_name")
        val typeName: String,
        @ColumnInfo(name = "first_stop_name")
        val firstStopName: String,
        @ColumnInfo(name = "last_stop_name")
        val lastStopName: String,
        @ColumnInfo(name = "variant_id")
        var variantIds: List<Int>
    )


    @Query("""
SELECT DISTINCT trips.route_id, first_stop_name, last_stop_name, trips.variant_id, r.route_type2_id, route_type2_name from trips
INNER JOIN variant_stops v ON v.variant_id = trips.variant_id AND v.variant_id IN (
	SELECT distinct variant_id FROM trips WHERE trip_id IN (
		SELECT DISTINCT trip_id FROM stop_times WHERE stop_id IN (
			SELECT DISTINCT stop_id FROM stops WHERE stop_name=:stopName
		)
	)
)
INNER JOIN routes r ON r.route_id = trips.route_id
INNER JOIN route_types ON route_types.route_type2_id = r.route_type2_id
ORDER BY trips.route_id""")
    fun getInfoForStopName(stopName: String): List<RouteInfo>
}