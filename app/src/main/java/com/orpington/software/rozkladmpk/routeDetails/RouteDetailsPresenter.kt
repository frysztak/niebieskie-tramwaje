package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.data.model.RouteDirections
import com.orpington.software.rozkladmpk.data.model.RouteInfo
import com.orpington.software.rozkladmpk.data.model.TimeTable
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource

class RouteDetailsPresenter(
    private val dataSource: RemoteDataSource
) : RouteDetailsContract.Presenter {

    private var infoView: RouteDetailsContract.InfoView? = null
    private var directionsView: RouteDetailsContract.DirectionsView? = null
    private var timetableView: RouteDetailsContract.TimetableView? = null

    private var routeID: String = ""
    private var stopName: String = ""
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
        //view.showProgressBar()
        dataSource.getRouteInfo(routeID, object : IDataSource.LoadDataCallback<RouteInfo> {
            override fun onDataLoaded(data: RouteInfo) {
                infoView?.showRouteInfo(data)
            }

            override fun onDataNotAvailable() {
                //view.reportThatSomethingWentWrong()
            }

        })
    }

    override fun loadRouteDirections() {
        //view.showProgressBar()
        dataSource.getRouteDirections(routeID, object : IDataSource.LoadDataCallback<RouteDirections> {
            override fun onDataLoaded(data: RouteDirections) {
                routeDirections = data.directions
                directionsView?.showRouteDirections(data)
            }

            override fun onDataNotAvailable() {
                //view.reportThatSomethingWentWrong()
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

        directionsView?.showTimetable(direction)
        infoView?.switchToTimetableTab()

        directionIdx = idx
    }

    override fun loadTimeTable() {
        timetableView?.showProgressBar()
        dataSource.getTimeTable(routeID, stopName, direction,
            object : IDataSource.LoadDataCallback<TimeTable> {
                override fun onDataLoaded(data: TimeTable) {
                    val helper = TimetableViewHelper()
                    timetableView?.showTimeTable(helper.processTimeTable(data))
                }

                override fun onDataNotAvailable() {
                    timetableView?.reportThatSomethingWentWrong()
                }
            })
    }
}