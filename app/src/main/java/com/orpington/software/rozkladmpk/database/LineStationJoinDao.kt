package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

/*
@Dao
interface LineStationJoinDao {
    @Insert
    fun insert(lineStationJoin: LineStationJoin)

    @Query("""
        SELECT id,name,info FROM stations
        INNER JOIN line_station_join ON stations.id=line_station_join.stationId
        WHERE line_station_join.lineId=:lineId
        ORDER BY ordinalNumber""")
    fun getStationsForLine(lineId: Int): List<Stop>

    @Query("""
        SELECT * FROM transport_lines
        INNER JOIN line_station_join ON transport_lines.id=line_station_join.lineId
        WHERE line_station_join.stationId=:stationId
        ORDER BY ordinalNumber
        """)
    fun getLinesForStation(stationId: Int): List<TransportLine>

    @Query("""
        SELECT name FROM stations
        WHERE id IN
        (
            (SELECT stationId FROM line_station_join
	        WHERE lineId=:lineId
	        ORDER BY ordinalNumber ASC
	        LIMIT 1),

			(SELECT stationId FROM line_station_join
	        WHERE lineId=:lineId
	        ORDER BY ordinalNumber DESC
	        LIMIT 1)
        )
    """)
    fun getFirstAndLastStationName(lineId: Int): List<String>

}
    */