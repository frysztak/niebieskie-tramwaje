package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import android.support.annotation.NonNull
import com.orpington.software.rozkladmpk.database.converters.DateTypeConverter
import com.orpington.software.rozkladmpk.database.converters.ListOfIntTypeConverter
import com.orpington.software.rozkladmpk.database.converters.TransportTypeConverter
import java.util.*

enum class TransportType {
    BUS,
    TRAM
}

@Entity(tableName = "transport_lines")
data class TransportLine constructor(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    @TypeConverters(TransportTypeConverter::class)
    val type: TransportType,

    @ColumnInfo(name = "last_updated")
    @TypeConverters(DateTypeConverter::class)
    val lastUpdated: Date,

    @ColumnInfo(name = "stations")
    @TypeConverters(ListOfIntTypeConverter::class)
    val stops: List<Int>
)
