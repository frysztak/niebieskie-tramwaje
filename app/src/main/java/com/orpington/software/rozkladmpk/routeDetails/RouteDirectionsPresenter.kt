package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.data.model.RouteDirections
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource

class RouteDirectionsPresenter(
    private val dataSource: RemoteDataSource,
    private val view: RouteDirectionsContract.View
) : RouteDirectionsContract.Presenter {

    private var routeDirections: List<String> = emptyList()

    override fun loadRouteDirections(routeID: String) {
        //currentRouteID = routeID
        //view.showProgressBar()
        dataSource.getRouteDirections(routeID, object : IDataSource.LoadDataCallback<RouteDirections> {
            override fun onDataLoaded(data: RouteDirections) {
                routeDirections = data.directions
                view.showRouteDirections(data)
            }

            override fun onDataNotAvailable() {
                //view.reportThatSomethingWentWrong()
            }

        })
    }

    override fun directionClicked(idx: Int) {
        val direction = routeDirections[idx]
        view.showTimetable(direction)
    }
}