package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.data.model.RouteDirections
import com.orpington.software.rozkladmpk.data.model.RouteInfo
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource

class RouteDetailsPresenter(
    private val dataSource: RemoteDataSource
) : RouteDetailsContract.Presenter {

    private var infoView: RouteDetailsContract.InfoView? = null
    private var directionsView: RouteDetailsContract.DirectionsView? = null

    private var routeID: String = ""
    private var stopName: String = ""
    private var routeDirections: List<String> = emptyList()
    private var direction: String = ""

    override fun attachInfoView(view: RouteDetailsContract.InfoView) {
        infoView = view
    }

    override fun attachDirectionsView(view: RouteDetailsContract.DirectionsView) {
        directionsView = view
    }

    override fun setRouteID(id: String) {
        routeID = id
    }

    override fun setStopName(name: String) {
        stopName = name
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

    override fun directionClicked(idx: Int) {
        direction = routeDirections[idx]
        directionsView?.showTimetable(direction)
    }
}