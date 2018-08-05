package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.Injection
import com.orpington.software.rozkladmpk.data.model.TimeTable
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import com.orpington.software.rozkladmpk.data.source.RouteDetailsState
import java.util.*

class RouteTimetablePresenter(
    private val dataSource: RemoteDataSource,
    private val state: RouteDetailsState
) : RouteTimetableContract.Presenter,
    Observer {

    private var view: RouteTimetableContract.View? = null
    override fun attachView(view: RouteTimetableContract.View) {
        this.view = view
        state.addObserver(this)
        // ???
        analyseState(Injection.getRouteDetailsState())
    }

    override fun dropView() {
        view = null
        state.deleteObserver(this)
    }

    private var timetable: TimeTable? = null
    override fun loadTimeTable() {
        view?.showProgressBar()
        dataSource.getTimeTable(
            state.routeID,
            state.stopName,
            state.direction,
            object : IDataSource.LoadDataCallback<TimeTable> {
                override fun onDataLoaded(data: TimeTable) {
                    val helper = TimetableViewHelper()
                    timetable = data
                    view?.hideProgressBar()
                    view?.showTimeTable(helper.processTimeTable(data))
                }

                override fun onDataNotAvailable() {
                    view?.hideProgressBar()
                    view?.reportThatSomethingWentWrong()
                }
            })
    }

    private var timeTag: String = ""
    /// time: PREFIX:HH:MM, e.g.
    /// WE:07:45
    /// SU:08:34
    override fun onTimeClicked(time: String) {
        val prefix = time.take(2)
        val timetableEntries = when (prefix) {
            TimetableViewHelper.DayType.Weekday.prefix ->
                timetable?.weekdays
            TimetableViewHelper.DayType.Saturday.prefix ->
                timetable?.saturdays
            TimetableViewHelper.DayType.Sunday.prefix ->
                timetable?.sundays
            else -> null
        }

        val hhmm = time.drop(3)
        val hhmmss = "$hhmm:00"
        val entry = timetableEntries?.find { entry ->
            entry.departureTime == hhmmss
        } ?: return

        Injection.getRouteDetailsState().tripID = entry.tripID
        //loadTimeline()
        if (timeTag.isNotEmpty()) {
            view?.unhighlightTime(timeTag)
        }
        view?.highlightTime(time)

        timeTag = time
    }

    override fun update(o: Observable?, arg: Any?) {
        val state = o as RouteDetailsState
        if (arg is String && arg == "direction") {
            analyseState(state)
        }
    }

    private fun analyseState(state: RouteDetailsState) {
        if (state.direction.isNotEmpty()) {
            loadTimeTable()
        }
    }

}