package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import android.support.annotation.NonNull
import com.orpington.software.rozkladmpk.database.converters.DateTypeConverter
import com.orpington.software.rozkladmpk.database.converters.ListOfStringTypeConverter
import com.orpington.software.rozkladmpk.database.converters.TransportTypeConverter
import java.util.*

enum class TransportType {
    BUS,
    TRAM
}

@Entity(tableName = "transport_line")
data class TransportLine constructor(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "line_name")
    val name: String,

    @ColumnInfo(name = "type")
    @TypeConverters(TransportTypeConverter::class)
    val type: TransportType,

    @ColumnInfo(name = "last_updated")
    @TypeConverters(DateTypeConverter::class)
    val lastUpdated: Date,

    @ColumnInfo(name = "stops")
    @TypeConverters(ListOfStringTypeConverter::class)
    val stops: List<String>
)
