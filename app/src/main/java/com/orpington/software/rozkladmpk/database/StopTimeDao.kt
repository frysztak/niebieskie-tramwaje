package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface StopTimeDao{
    @Insert
    fun insert(stoptimes: List<StopTime>)

    @Query("SELECT DISTINCT trip_id FROM stop_times WHERE stop_id IN (:stopIds)")
    fun getTripIdsForStopIds(stopIds: List<Int>): List<Int>
}
