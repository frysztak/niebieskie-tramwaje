package software.orpington.rozkladmpk.home

import software.orpington.rozkladmpk.data.model.StopsAndRoutes
import software.orpington.rozkladmpk.data.source.IDataSource
import software.orpington.rozkladmpk.data.source.RemoteDataSource

class SearchPresenter(
    private val dataSource: RemoteDataSource
) : SearchContract.Presenter {
    private var view: SearchContract.View? = null
    override fun attachView(view: SearchContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }


    private var stopsAndRoutes: List<StopOrRouteViewItem> = emptyList()
    override fun loadData() {
        dataSource.getStopsAndRoutes(object : IDataSource.LoadDataCallback<StopsAndRoutes> {
            override fun onDataLoaded(data: StopsAndRoutes) {
                val helper = StopsAndRoutesHelper()
                stopsAndRoutes = helper.convertModel(data)
            }

            override fun onDataNotAvailable() {
                // retry
                loadData()
            }
        })
    }

    override fun queryTextChanged(newText: String) {
        if (newText.isEmpty()) {
            view?.hideSearchResults()
            return
        }

        val helper = StopsAndRoutesHelper()
        val searchResults = helper.filterItems(stopsAndRoutes, newText).take(10)

        if (searchResults.isNotEmpty()) {
            view?.showSearchResults(searchResults)
        } else {
            view?.showStopNotFound()
        }
    }

    override fun routeClicked(routeID: String) {
        view?.navigateToStopsForRoute(routeID)
    }

    override fun stopClicked(stopName: String) {
        view?.navigateToRouteVariants(stopName)
    }
}