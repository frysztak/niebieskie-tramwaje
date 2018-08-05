package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.Injection
import com.orpington.software.rozkladmpk.data.model.RouteDirections
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import com.orpington.software.rozkladmpk.data.source.RouteDetailsState

class RouteDirectionsPresenter(
    private val dataSource: RemoteDataSource,
    private val state: RouteDetailsState
) : RouteDirectionsContract.Presenter {

    private var view: RouteDirectionsContract.View? = null
    override fun attachView(view: RouteDirectionsContract.View) {
        this.view = view
    }

    override fun dropView() {
        view = null
    }

    private var routeDirections: List<String> = emptyList()
    override fun loadRouteDirections() {
        view?.showProgressBar()
        dataSource.getRouteDirectionsThroughStop(
            state.routeID,
            state.stopName,
            object : IDataSource.LoadDataCallback<RouteDirections> {
                override fun onDataLoaded(data: RouteDirections) {
                    routeDirections = data.directions
                    view?.hideProgressBar()
                    view?.showRouteDirections(data,
                        Injection.getRouteDetailsState().directionIdx)
                }

                override fun onDataNotAvailable() {
                    view?.hideProgressBar()
                    view?.reportThatSomethingWentWrong()
                }
            }
        )
    }

    private var directionIdx = -1
    override fun onDirectionClicked(idx: Int) {
        val direction = routeDirections[idx]
        view?.directionSelected(direction)
        //Injection.getRouteDetailsState().direction = direction
        //Injection.getRouteDetailsState().directionIdx = idx
        //loadTimeTable()

        if (directionIdx != -1) {
            view?.unhighlightDirection(directionIdx)
        }
        view?.highlightDirection(idx)
        //infoView?.switchToTimetableTab()

        directionIdx = idx
    }

}