package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.data.model.RouteInfo
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import com.orpington.software.rozkladmpk.data.source.RouteDetailsState
import java.util.*

class RouteDetailsPresenter(
    private val dataSource: RemoteDataSource,
    private val state: RouteDetailsState
) : RouteDetailsContract.Presenter,
    Observer {

    private var view: RouteDetailsContract.View? = null
    override fun attachView(view: RouteDetailsContract.View) {
        this.view = view
        state.addObserver(this)
    }

    override fun dropView() {
        view = null
        state.deleteObserver(this)
    }


    override fun update(o: Observable?, arg: Any?) {
        val state = o as RouteDetailsState
        //if (state.tripID.isNotEmpty()) {
        //    view?.switchToTimelineTab()
        //} else if (state.direction.isNotEmpty()) {
        //    view?.switchToTimetableTab()
        //}
    }

    override fun loadRouteInfo() {
        view?.showProgressBar()
        dataSource.getRouteInfo(
            state.routeID,
            object : IDataSource.LoadDataCallback<RouteInfo> {
                override fun onDataLoaded(data: RouteInfo) {
                    view?.hideProgressBar()
                    view?.showRouteInfo(data)
                }

                override fun onDataNotAvailable() {
                    view?.hideProgressBar()
                    view?.reportThatSomethingWentWrong()
                }
            }
        )
    }
}