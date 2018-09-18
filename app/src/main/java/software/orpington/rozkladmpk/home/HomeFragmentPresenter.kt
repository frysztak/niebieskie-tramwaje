package software.orpington.rozkladmpk.home

import software.orpington.rozkladmpk.data.model.StopsAndRoutes
import software.orpington.rozkladmpk.data.source.IDataSource
import software.orpington.rozkladmpk.data.source.RemoteDataSource

class HomeFragmentPresenter(
    private val dataSource: RemoteDataSource
) : HomeFragmentContract.Presenter {
    private var view: HomeFragmentContract.View? = null
    override fun attachView(view: HomeFragmentContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    private var stopsAndRoutes: List<StopOrRoute> = emptyList()
    override fun loadData() {
        view?.showProgressBar()
        dataSource.getStopsAndRoutes(object : IDataSource.LoadDataCallback<StopsAndRoutes> {
            override fun onDataLoaded(data: StopsAndRoutes) {
                view?.hideProgressBar()
                val helper = StopsAndRoutesHelper()
                stopsAndRoutes = helper.convertModel(data)
            }

            override fun onDataNotAvailable() {
                view?.hideProgressBar()
                view?.reportThatSomethingWentWrong()
            }
        })
    }

    override fun queryTextChanged(newText: String) {
        if (newText.isEmpty()) {
            view?.hideSearchResults()
            return
        }

        val helper = StopsAndRoutesHelper()
        val searchResults = helper.filterItems(stopsAndRoutes, newText)

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

    override fun onFavouritesLoaded(kvs: Map<String, *>) {
        val favourites: MutableList<FavouriteItem> = mutableListOf()

        kvs.filterKeys { key ->
            key.startsWith("fav_")
        }.forEach { (key, value) ->
            val parts = key.split("_")
            val routeID = parts[1]
            val stopName = parts[2]
            val isBus = parts[3] == "bus"

            val directions = value as Set<String>?
                ?: // something is very very wrong
                return

            for (direction in directions) {
                favourites.add(FavouriteItem(
                    routeID, isBus, stopName, direction
                ))
            }
        }

        view?.showFavourites(favourites)
    }
}