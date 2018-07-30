package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.data.model.TimeTable
import com.orpington.software.rozkladmpk.data.model.TimeTableEntry
import java.util.*

class TimetableViewHelper {

    private lateinit var timeTable: TimeTable
    private var items: MutableList<ViewItem> = arrayListOf()

    enum class ViewType(val code: Int) {
        HEADER(0),
        ROW(1)
    }

    interface ViewItem {
        val type: ViewType
    }

    class HeaderItem(val dayType: DayType, val additionalText: String) : ViewItem {
        override val type: ViewType = ViewType.HEADER
    }

    class RowItem(val data: Row) : ViewItem {
        override val type: ViewType = ViewType.ROW
    }

    enum class DayType {
        Weekday,
        Saturday,
        Sunday
    }

    fun processTimeTable(tt: TimeTable): List<ViewItem> {
        timeTable = tt

        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)

        val addHeaderAndRows = { dayType: DayType, timeTableEntries: List<TimeTableEntry>? ->
            if (timeTableEntries != null) {
                items.add(HeaderItem(dayType, getHeaderAdditionalInfo(day, dayType)))
                items.addAll(processSingleTimeTable(timeTableEntries))
            }
        }

        addHeaderAndRows(DayType.Weekday, timeTable.weekdays)
        addHeaderAndRows(DayType.Saturday, timeTable.saturdays)
        addHeaderAndRows(DayType.Sunday, timeTable.sundays)

        return items
    }


    private fun processSingleTimeTable(data: List<TimeTableEntry>): List<ViewItem> {
        val groups = data.groupBy { entry ->
            entry.arrivalTime.split(":")[0]
        }.mapValues { (key, value) ->
            value.map { entry ->
                entry.arrivalTime.split(":")[1]
            }
        }

        var items: MutableList<ViewItem> = arrayListOf()
        for ((key, value) in groups) {
            var row: Row = arrayListOf()
            row.add(key)
            row.addAll(value)
            items.add(RowItem(row))
        }
        return items
    }

    private fun getHeaderAdditionalInfo(currentDay: Int, timetableDay: DayType): String {
        val weekdays = listOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
            Calendar.THURSDAY, Calendar.FRIDAY)
        val saturday = Calendar.SATURDAY
        val sunday = Calendar.SUNDAY

        when (timetableDay) {
            DayType.Weekday -> {
                if (weekdays.contains(currentDay)) return "today"
            }
            DayType.Saturday -> {
                if (currentDay == saturday) return "today"
            }
            DayType.Sunday -> {
                if (currentDay == sunday) return "today"
            }
        }

        return ""
    }
}