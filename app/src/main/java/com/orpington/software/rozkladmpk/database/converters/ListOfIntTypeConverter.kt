package com.orpington.software.rozkladmpk.database.converters

import android.arch.persistence.room.TypeConverter

class ListOfIntTypeConverter {
    @TypeConverter
    fun listOfIntToString(list: List<Int>?): String
    {
        if (list == null) {
            return ""
        }

        return list.joinToString(",")
    }

    @TypeConverter
    fun stringToListOfInt(str: String): List<Int>
    {
        return str.split(",").map { s -> s.toInt() }
    }
}