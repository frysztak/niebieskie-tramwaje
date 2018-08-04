package com.orpington.software.rozkladmpk.stopsAndRoutes

import com.orpington.software.rozkladmpk.data.model.StopNames
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import javax.inject.Inject


class StopsAndRoutesPresenter
@Inject constructor(
    internal val dataSource: RemoteDataSource
) : StopsAndRoutesContract.Presenter {
    private var allStops: List<String> = emptyList()
    private var shownStops: List<String> = emptyList()
    //private var generalRoutes: List<Route> = emptyList()

    private lateinit var view: StopsAndRoutesContract.View

    override fun attachView(view: StopsAndRoutesContract.View) {
        this.view = view
    }

    override fun setAllStopNames(names: List<String>) {
        allStops = names
    }

    override fun setShownStopNames(names: List<String>) {
        shownStops = names
    }

    override fun loadStopNames() {
        if (allStops.isNotEmpty()) return
        view.showProgressBar()
        dataSource.getStopNames(object : IDataSource.LoadDataCallback<StopNames> {
            override fun onDataLoaded(data: StopNames) {
                setAllStopNames(data.stopNames)
                setShownStopNames(allStops)
                view.hideProgressBar()
                view.displayStops(shownStops)
            }

            override fun onDataNotAvailable() {
                view.hideProgressBar()
                view.reportThatSomethingWentWrong()
            }
        })
    }

    override fun queryTextChanged(newText: String) {
        setShownStopNames(allStops.filter { it.startsWith(newText, true) })
        if (shownStops.isNotEmpty()) {
            view.displayStops(shownStops)
        } else {
            view.showStopNotFound()
        }
    }

    override fun listItemClicked(position: Int) {
        if (position >= shownStops.size) return
        val stopName = shownStops[position]
        view.navigateToRouteVariants(stopName)
    }

}

