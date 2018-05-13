package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE trip_id IN (:tripIds)")
    fun getTripsGivenIds(tripIds: List<Int>): List<Trip>
}