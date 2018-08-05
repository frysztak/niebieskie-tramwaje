package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.Injection
import com.orpington.software.rozkladmpk.data.model.Timeline
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import com.orpington.software.rozkladmpk.data.source.RouteDetailsState
import java.util.*

class RouteTimelinePresenter(
    private val dataSource: RemoteDataSource
) : RouteTimelineContract.Presenter,
    Observer {

    private var view: RouteTimelineContract.View? = null
    override fun attachView(view: RouteTimelineContract.View) {
        this.view = view
        Injection.getRouteDetailsState().addObserver(this)
    }

    override fun dropView() {
        view = null
        Injection.getRouteDetailsState().deleteObserver(this)
    }

    override fun update(o: Observable?, arg: Any?) {
        val state = o as RouteDetailsState
        if (state.tripID.isNotEmpty()) {
            loadTimeline()
        }
    }

    override fun loadTimeline() {
        view?.showProgressBar()
        dataSource.getTripTimeline(
            Injection.getRouteDetailsState().tripID,
            object : IDataSource.LoadDataCallback<Timeline> {
                override fun onDataLoaded(data: Timeline) {
                    view?.hideProgressBar()
                    view?.showTimeline(data)
                }

                override fun onDataNotAvailable() {
                    view?.hideProgressBar()
                    view?.reportThatSomethingWentWrong()
                }
            })
    }

}