package com.orpington.software.rozkladmpk.database.converters

import android.arch.persistence.room.TypeConverter

class ListOfStringTypeConverter {
    @TypeConverter
    fun listOfStringsToString(list: List<String>?): String
    {
        if (list == null) {
            return ""
        }

        return list.joinToString()
    }

    @TypeConverter
    fun stringToListOfStrings(str: String): List<String>
    {
        return str.split(",")
    }
}