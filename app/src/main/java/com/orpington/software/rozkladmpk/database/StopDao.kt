package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface StopDao {
    @Insert
    fun insert(lines: List<Stop>)

    @Query("SELECT DISTINCT stop_name from stops")
    fun getAllStopNames(): List<String>

    @Query("SELECT DISTINCT stop_name from stops WHERE stop_name LIKE :name || '%'")
    fun getStopNamesStartingWith(name: String): List<String>

    @Query("SELECT stop_name from stops WHERE stop_id=:stopId")
    fun getStopName(stopId: Int): String
}