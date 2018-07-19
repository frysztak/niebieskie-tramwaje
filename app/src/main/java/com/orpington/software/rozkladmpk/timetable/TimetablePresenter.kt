package com.orpington.software.rozkladmpk.timetable

import com.orpington.software.rozkladmpk.data.model.TimeTable
import com.orpington.software.rozkladmpk.data.model.TimeTableEntry
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource

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
                    view.showTimeTable(processTimeTable(timeTable))
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

    class HeaderItem(val text: String) : ViewItem {
        override val type: ViewType = ViewType.HEADER
    }

    class RowItem(val data: Row) : ViewItem {
        override val type: ViewType = ViewType.ROW
    }

    private fun processTimeTable(timeTable: TimeTable): List<ViewItem> {
        var items: MutableList<ViewItem> = arrayListOf()
        if (timeTable.weekdays != null) {
            items.add(HeaderItem("Weekdays"))
            items.addAll(processSingleTimeTable(timeTable.weekdays))
        }

        if (timeTable.saturdays != null) {
            items.add(HeaderItem("Saturday"))
            items.addAll(processSingleTimeTable(timeTable.saturdays))
        }

        if (timeTable.sundays != null) {
            items.add(HeaderItem("Sunday"))
            items.addAll(processSingleTimeTable(timeTable.sundays))
        }

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

}