package com.orpington.software.rozkladmpk.stopsAndRoutes

import com.orpington.software.rozkladmpk.data.model.StopsAndRoutes
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource


class StopsAndRoutesPresenter(
    private var dataSource: RemoteDataSource
) : StopsAndRoutesContract.Presenter {

    private var view: StopsAndRoutesContract.View? = null
    override fun attachView(view: StopsAndRoutesContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    private var stopsAndRoutes: List<StopOrRoute> = emptyList()
    override fun setStopsAndRoutes(data: StopsAndRoutes) {
        val helper = StopsAndRoutesHelper()
        stopsAndRoutes = helper.convertModel(data)
    }

    private var shownStopsAndRoutes: List<StopOrRoute> = emptyList()
    override fun setShownStopsAndRoutes(data: List<StopOrRoute>) {
        shownStopsAndRoutes = data
    }

    override fun loadStopsAndRoutes() {
        //if (allStops.isNotEmpty()) return
        view?.showProgressBar()
        dataSource.getStopsAndRoutes(object : IDataSource.LoadDataCallback<StopsAndRoutes> {
            override fun onDataLoaded(data: StopsAndRoutes) {
                setStopsAndRoutes(data)
                setShownStopsAndRoutes(stopsAndRoutes)
                view?.hideProgressBar()
                view?.displayStopsAndRoutes(shownStopsAndRoutes)
            }

            override fun onDataNotAvailable() {
                view?.hideProgressBar()
                view?.reportThatSomethingWentWrong()
            }
        })
    }

    override fun queryTextChanged(newText: String) {

        val helper = StopsAndRoutesHelper()
        shownStopsAndRoutes = helper.filterItems(stopsAndRoutes, newText)

        if (shownStopsAndRoutes.isNotEmpty()) {
            view?.displayStopsAndRoutes(shownStopsAndRoutes)
        } else {
            view?.showStopNotFound()
        }
    }

    override fun listItemClicked(position: Int) {
        if (position >= shownStopsAndRoutes.size) return

        val item = shownStopsAndRoutes[position]
        when (item) {
            is Stop -> view?.navigateToRouteVariants(item.stopName)
            is Route -> view?.navigateToStopsForRoute(item.routeID)
        }
    }

}

