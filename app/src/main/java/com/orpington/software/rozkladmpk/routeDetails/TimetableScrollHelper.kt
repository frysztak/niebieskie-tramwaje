package com.orpington.software.rozkladmpk.routeDetails

import java.util.*
import com.orpington.software.rozkladmpk.routeDetails.TimetableViewHelper.*

data class TimeIndices(val hourIdx: Int, val minuteIdx: Int)

class TimetableHelper(private val calendar: Calendar) {
    private data class Time(val hour: Int, val minute: Int)

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

    fun calculateRowAndColumnToScrollInto(items: List<ViewItem>): TimeIndices {
        val dayTypeToFind = getCurrentDayType()
        val headerIndex = items.indexOfFirst { item ->
            item is HeaderItem && item.dayType == dayTypeToFind
        }

        if (headerIndex == -1) return TimeIndices(-1, -1)

        val currentTime = Time(
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )

        val timeComparer = { time1: Time, time2: Time ->
            val cal1 = Calendar.getInstance()
            cal1.set(Calendar.HOUR_OF_DAY, time1.hour)
            cal1.set(Calendar.MINUTE, time1.minute)

            val cal2 = Calendar.getInstance()
            cal2.set(Calendar.HOUR_OF_DAY, time2.hour)
            cal2.set(Calendar.MINUTE, time2.minute)

            cal1.after(cal2)
        }

        for (hourIdx in headerIndex until items.size) {
            val item = items[hourIdx]
            if (item.type != ViewType.ROW) continue

            val rowItem = item as RowItem
            val hour = rowItem.data.first().toInt()

            for (minuteIdx in 1 until rowItem.data.size) {
                val minute = rowItem.data[minuteIdx].toInt()
                val time = Time(hour, minute)
                if (timeComparer(time, currentTime)) {
                    return TimeIndices(hourIdx, minuteIdx - 1)
                }
            }
        }

        return TimeIndices(-1, -1)
    }
}