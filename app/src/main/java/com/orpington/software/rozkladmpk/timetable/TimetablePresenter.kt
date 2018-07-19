package com.orpington.software.rozkladmpk.timetable

import com.orpington.software.rozkladmpk.data.model.TimeTable
import com.orpington.software.rozkladmpk.data.model.TimeTableEntry
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import java.util.*

class TimetablePresenter(
    private var dataSource: RemoteDataSource,
    private var view: TimetableContract.View
) : TimetableContract.Presenter {

    private lateinit var timeTable: TimeTable

    override fun setTimeTable(newTimeTable: TimeTable) {
        timeTable = newTimeTable
    }

    override fun getTimeTable(): TimeTable {
        return timeTable
    }

    override fun loadTimeTable(routeID: String, atStop: String, fromStop: String, toStop: String) {
        view.showProgressBar()
        dataSource.getTimeTable(routeID, atStop, fromStop, toStop,
            object : IDataSource.LoadDataCallback<TimeTable> {
                override fun onDataLoaded(data: TimeTable) {
                    setTimeTable(data)
                    val (rows, indexToScrollInto) = processTimeTable(timeTable)
                    view.showTimeTable(rows, indexToScrollInto)
                    view.hideProgressBar()
                }

                override fun onDataNotAvailable() {
                    view.reportThatSomethingWentWrong()
                    view.hideProgressBar()
                }
            })
    }

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

    private fun processTimeTable(timeTable: TimeTable): Pair<List<ViewItem>, Int> {
        var items: MutableList<ViewItem> = arrayListOf()
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)

        if (timeTable.weekdays != null) {
            items.add(HeaderItem(DayType.Weekday, getHeaderAdditionalInfo(day, DayType.Weekday)))
            items.addAll(processSingleTimeTable(timeTable.weekdays))
        }

        if (timeTable.saturdays != null) {
            items.add(HeaderItem(DayType.Saturday, getHeaderAdditionalInfo(day, DayType.Saturday)))
            items.addAll(processSingleTimeTable(timeTable.saturdays))
        }

        if (timeTable.sundays != null) {
            items.add(HeaderItem(DayType.Sunday, getHeaderAdditionalInfo(day, DayType.Sunday)))
            items.addAll(processSingleTimeTable(timeTable.sundays))
        }

        // get position of a row corresponding to current day and time,
        // so that we can scroll into it

        val dayTypeToFind = getCurrentDayType()
        val headerIndex = items.indexOfFirst { item ->
            var result = false
            if (item is HeaderItem) {
                result = item.dayType == dayTypeToFind
            }
            result
        }

        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val hourIndex = items.drop(headerIndex).indexOfFirst { item ->
            var result = false
            if (item is RowItem) {
                result = item.data[0].toInt() == currentHour
            }
            result
        }

        return Pair(items, hourIndex)
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

    private fun getCurrentDayType(): DayType {
        val calendar = Calendar.getInstance()
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