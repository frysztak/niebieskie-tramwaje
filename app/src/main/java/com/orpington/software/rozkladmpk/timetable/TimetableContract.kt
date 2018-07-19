package com.orpington.software.rozkladmpk.timetable

import com.orpington.software.rozkladmpk.data.model.TimeTable

interface TimetableContract {
    interface Presenter {
        fun loadTimeTable(routeID: String, atStop: String, fromStop: String, toStop: String)
        fun setTimeTable(timeTable: TimeTable)
        fun getTimeTable(): TimeTable
    }

    interface View {
        fun showProgressBar()
        fun hideProgressBar()

        fun showTimeTable(timeTable: TimeTable)
        fun reportThatSomethingWentWrong()
    }
}