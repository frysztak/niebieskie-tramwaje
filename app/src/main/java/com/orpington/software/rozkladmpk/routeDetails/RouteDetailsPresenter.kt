package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.data.model.RouteDirections
import com.orpington.software.rozkladmpk.data.model.RouteInfo
import com.orpington.software.rozkladmpk.data.model.TimeTable
import com.orpington.software.rozkladmpk.data.model.Timeline
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource

class RouteDetailsPresenter(
    private val dataSource: RemoteDataSource
) : RouteDetailsContract.Presenter {

    private var infoView: RouteDetailsContract.InfoView? = null
    private var directionsView: RouteDetailsContract.DirectionsView? = null
    private var timetableView: RouteDetailsContract.TimetableView? = null
    private var timelineView: RouteDetailsContract.TimelineView? = null

    private var state = RouteDetailsState()

    override fun attachInfoView(view: RouteDetailsContract.InfoView) {
        infoView = view
    }

    override fun attachDirectionsView(view: RouteDetailsContract.DirectionsView) {
        directionsView = view
    }

    override fun attachTimetableView(view: RouteDetailsContract.TimetableView) {
        timetableView = view
    }

    override fun attachTimelineView(view: RouteDetailsContract.TimelineView) {
        timelineView = view
    }

    override fun setRouteID(id: String) {
        state.routeID = id
    }

    override fun setStopName(name: String) {
        state.stopName = name
    }

    override fun loadRouteInfo() {
        infoView?.showProgressBar()
        dataSource.getRouteInfo(state.routeID, object : IDataSource.LoadDataCallback<RouteInfo> {
            override fun onDataLoaded(data: RouteInfo) {
                infoView?.hideProgressBar()
                infoView?.showRouteInfo(data)
            }

            override fun onDataNotAvailable() {
                infoView?.hideProgressBar()
                infoView?.reportThatSomethingWentWrong()
            }
        })
    }

    override fun loadRouteDirections() {
        directionsView?.showProgressBar()
        dataSource.getRouteDirectionsThroughStop(state.routeID, state.stopName, object : IDataSource.LoadDataCallback<RouteDirections> {
            override fun onDataLoaded(data: RouteDirections) {
                state.routeDirections = data.directions
                directionsView?.hideProgressBar()
                directionsView?.showRouteDirections(state.routeDirections, state.currentRouteDirection)
            }

            override fun onDataNotAvailable() {
                directionsView?.hideProgressBar()
                directionsView?.reportThatSomethingWentWrong()
            }
        })
    }

    override fun onDirectionClicked(directionIdx: Int) {
        state.currentRouteDirection = directionIdx
        loadTimeTable()

        directionsView?.highlightDirection(directionIdx)
        infoView?.switchToTimetableTab()
    }

    override fun loadTimeTable() {
        timetableView?.showProgressBar()
        val direction = state.routeDirections[state.currentRouteDirection]
        dataSource.getTimeTable(state.routeID, state.stopName, direction,
            object : IDataSource.LoadDataCallback<TimeTable> {
                override fun onDataLoaded(data: TimeTable) {
                    val helper = TimetableViewHelper()
                    state.timetable = data
                    timetableView?.hideProgressBar()
                    timetableView?.showTimeTable(helper.processTimeTable(data))
                }

                override fun onDataNotAvailable() {
                    timetableView?.hideProgressBar()
                    timetableView?.reportThatSomethingWentWrong()
                }
            })
    }

    /// time: PREFIX:HH:MM, e.g.
    /// WE:07:45
    /// SU:08:34
    override fun onTimeClicked(time: String) {
        val prefix = time.take(2)
        val timetableEntries = when (prefix) {
            TimetableViewHelper.DayType.Weekday.prefix ->
                state.timetable?.weekdays
            TimetableViewHelper.DayType.Saturday.prefix ->
                state.timetable?.saturdays
            TimetableViewHelper.DayType.Sunday.prefix ->
                state.timetable?.sundays
            else -> null
        }

        val hhmm = time.drop(3)
        val hhmmss = "$hhmm:00"
        val entry = timetableEntries?.find { entry ->
            entry.departureTime == hhmmss
        } ?: return

        state.tripID = entry.tripID
        loadTimeline()
        if (state.currentTimeTag.isNotEmpty()) {
            timetableView?.unhighlightTime(state.currentTimeTag)
        }
        timetableView?.highlightTime(time)
        infoView?.switchToTimelineTab()

        state.currentTimeTag = time
    }

    override fun loadTimeline() {
        timelineView?.showProgressBar()
        dataSource.getTripTimeline(state.tripID,
            object : IDataSource.LoadDataCallback<Timeline> {
                override fun onDataLoaded(data: Timeline) {
                    timelineView?.hideProgressBar()
                    timelineView?.showTimeline(data)
                }

                override fun onDataNotAvailable() {
                    timelineView?.hideProgressBar()
                    timelineView?.reportThatSomethingWentWrong()
                }
            })
    }

    override fun getState(): RouteDetailsState {
        return state
    }

    override fun setState(state: RouteDetailsState) {
        this.state = state
    }
}