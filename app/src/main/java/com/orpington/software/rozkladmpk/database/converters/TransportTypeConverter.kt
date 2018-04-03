package com.orpington.software.rozkladmpk.database.converters

import android.arch.persistence.room.TypeConverter
import com.orpington.software.rozkladmpk.database.TransportType

class TransportTypeConverter {
    @TypeConverter
    fun transportTypeToString(type: TransportType): String {
        return if (type == TransportType.BUS) "bus" else "tram"
    }

    @TypeConverter
    fun stringToTransportType(str: String): TransportType {
        return if (str == "bus") TransportType.BUS else TransportType.TRAM
    }
}