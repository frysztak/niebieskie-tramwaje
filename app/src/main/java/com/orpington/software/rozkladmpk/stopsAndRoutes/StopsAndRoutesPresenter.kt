package com.orpington.software.rozkladmpk.stopsAndRoutes

import com.orpington.software.rozkladmpk.data.model.StopNames
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.data.source.RepositoryDataSource


class StopsAndRoutesPresenter(
    private var repository: RepositoryDataSource,
    private var view: StopsAndRoutesContract.View
) : StopsAndRoutesContract.Presenter {
    private var allStops: List<String> = emptyList()
    private var stops: List<String> = emptyList()
    //private var generalRoutes: List<Route> = emptyList()

    override fun queryTextChanged(newText: String) {
        if (allStops.isEmpty()) {
            repository.getAllStopNames(object : IDataSource.LoadDataCallback<StopNames> {
                override fun onDataLoaded(data: StopNames) {
                    allStops = data.stopNames
                }

                override fun onDataNotAvailable() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }, true)
        }
        if (newText != null) {
            //stops = interactor.getStopNamesStartingWith(newText)
            //generalRoutes = interactor.getLinesStartingWith(newText)
            stops = allStops.filter { it.startsWith(newText, true) }
            view.displayStops(stops)
        }
    }

    override fun listItemClicked(position: Int) {
        val stopName = stops[position]
        view.navigateToRouteVariants(stopName)
    }

}

