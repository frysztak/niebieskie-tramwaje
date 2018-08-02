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

    private var routeID: String = ""
    private var stopName: String = ""
    private var timetable: TimeTable? = null
    private var tripID: String = ""
    private var routeDirections: List<String> = emptyList()

    private var directionIdx = -1
    private var direction: String = ""

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
        routeID = id
    }

    override fun setStopName(name: String) {
        stopName = name
    }

    override fun setDirection(dir: String) {
        direction = dir
    }

    override fun loadRouteInfo() {
        infoView?.showProgressBar()
        dataSource.getRouteInfo(routeID, object : IDataSource.LoadDataCallback<RouteInfo> {
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
        dataSource.getRouteDirectionsThroughStop(routeID, stopName, object : IDataSource.LoadDataCallback<RouteDirections> {
            override fun onDataLoaded(data: RouteDirections) {
                routeDirections = data.directions
                directionsView?.hideProgressBar()
                directionsView?.showRouteDirections(data)
            }

            override fun onDataNotAvailable() {
                directionsView?.hideProgressBar()
                directionsView?.reportThatSomethingWentWrong()
            }
        })
    }

    override fun onDirectionClicked(idx: Int) {
        direction = routeDirections[idx]
        loadTimeTable()

        if (directionIdx != -1) {
            directionsView?.unhighlightDirection(directionIdx)
        }
        directionsView?.highlightDirection(idx)
        infoView?.switchToTimetableTab()

        directionIdx = idx
    }

    override fun loadTimeTable() {
        timetableView?.showProgressBar()
        dataSource.getTimeTable(routeID, stopName, direction,
            object : IDataSource.LoadDataCallback<TimeTable> {
                override fun onDataLoaded(data: TimeTable) {
                    val helper = TimetableViewHelper()
                    timetable = data
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
        }

        if (entry != null) {
            tripID = entry.tripID
            loadTimeline()
            infoView?.switchToTimelineTab()
        }
    }

    override fun loadTimeline() {
        timelineView?.showProgressBar()
        dataSource.getTripTimeline(tripID,
            object : IDataSource.LoadDataCallback<Timeline> {
                override fun onDataLoaded(data: Timeline) {
                    timelineView?.showTimeline(data)
                }

                override fun onDataNotAvailable() {
                    timelineView?.reportThatSomethingWentWrong()
                }
            })
    }
}