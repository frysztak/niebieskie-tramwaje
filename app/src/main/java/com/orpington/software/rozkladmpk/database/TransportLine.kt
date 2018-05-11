package com.orpington.software.rozkladmpk.database

import android.arch.persistence.room.*
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
    @ColumnInfo(name = "id")
    val id: Int,

    @NonNull
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    @TypeConverters(TransportTypeConverter::class)
    val type: TransportType
)
