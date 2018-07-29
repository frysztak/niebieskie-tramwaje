package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.data.model.RouteDirections
import com.orpington.software.rozkladmpk.data.model.RouteInfo
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource

class RouteDetailsPresenter(
    private val dataSource: RemoteDataSource,
    private val view: RouteDetailsContract.View
) : RouteDetailsContract.Presenter {

    private var currentRouteID: String = ""

    override fun loadRouteDetails(routeID: String) {
        currentRouteID = routeID
        //view.showProgressBar()
        dataSource.getRouteInfo(routeID, object : IDataSource.LoadDataCallback<RouteInfo> {
            override fun onDataLoaded(data: RouteInfo) {
                view.showRouteInfo(data)
            }

            override fun onDataNotAvailable() {
                //view.reportThatSomethingWentWrong()
            }

        })
    }

    override fun loadRouteDirections(routeID: String) {
        //currentRouteID = routeID
        //view.showProgressBar()
        dataSource.getRouteDirections(routeID, object : IDataSource.LoadDataCallback<RouteDirections> {
            override fun onDataLoaded(data: RouteDirections) {
                view.showRouteDirections(data)
            }

            override fun onDataNotAvailable() {
                //view.reportThatSomethingWentWrong()
            }

        })
    }
}