package com.orpington.software.rozkladmpk.timetable

import com.orpington.software.rozkladmpk.timetable.TimetablePresenter.*
import java.util.*

class TimetableHelper(private val calendar: Calendar) {
    fun getCurrentDayType(): DayType {
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)

        val weekdays = listOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
            Calendar.THURSDAY, Calendar.FRIDAY)

        return when {
            weekdays.contains(currentDay) -> DayType.Weekday
            currentDay == Calendar.SATURDAY -> DayType.Saturday
            currentDay == Calendar.SUNDAY -> DayType.Sunday
            else -> throw Exception("Unknown day: $currentDay")
        }
    }

    fun calculateRowToScrollInto(items: List<ViewItem>): Int {
        val dayTypeToFind = getCurrentDayType()
        val headerIndex = items.indexOfFirst { item ->
            item is HeaderItem && item.dayType == dayTypeToFind
        }
        if (headerIndex == -1) return -1

        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        return items.drop(headerIndex).indexOfFirst { item ->
            item is RowItem && item.data[0].toInt() == currentHour
        }
    }
}