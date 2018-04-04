package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface TransportLineDao {
    @Insert
    fun insert(lines: List<TransportLine>)

    @Query("SELECT * FROM transport_line")
    fun getAll(): List<TransportLine>

    @Query("SELECT * FROM transport_line WHERE line_name LIKE :name || '%' ORDER BY line_name")
    fun findByName(name: String): List<TransportLine>
}