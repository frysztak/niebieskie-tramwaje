package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface StationDao {
    @Insert
    fun insert(lines: List<Station>)

    @Query("SELECT DISTINCT name from stations")
    fun getAllStationNames(): List<String>

    @Query("SELECT DISTINCT name from stations WHERE name LIKE :name || '%'")
    fun getStationNamesStartingWith(name: String): List<String>
}