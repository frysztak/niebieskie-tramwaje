package com.orpington.software.rozkladmpk.timetable

import com.orpington.software.rozkladmpk.data.model.TimeTable
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
                    view.showTimeTable(timeTable)
                    view.hideProgressBar()
                }

                override fun onDataNotAvailable() {
                    view.reportThatSomethingWentWrong()
                    view.hideProgressBar()
                }
            })
    }

}