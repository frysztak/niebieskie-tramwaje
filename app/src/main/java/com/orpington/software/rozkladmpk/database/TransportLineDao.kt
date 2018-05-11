package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface TransportLineDao {
    @Insert
    fun insert(lines: List<TransportLine>)

    @Query("SELECT * FROM transport_lines")
    fun getAll(): List<TransportLine>

    @Query("SELECT * FROM transport_lines WHERE name LIKE :name || '%' ORDER BY name")
    fun findByName(name: String): List<TransportLine>

    @Query("SELECT name FROM transport_lines WHERE id=:lineId")
    fun getName(lineId: Int): String
}