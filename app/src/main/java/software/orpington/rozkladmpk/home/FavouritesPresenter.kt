package software.orpington.rozkladmpk.home

class FavouritesPresenter: FavouritesContract.Presenter {
    private var view: FavouritesContract.View? = null
    override fun attachView(view: FavouritesContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    private var favourites: List<FavouriteItem> = emptyList()
    override fun onFavouritesLoaded(preferencesMap: Map<String, *>) {
        val favourites: MutableList<FavouriteItem> = mutableListOf()

        preferencesMap.filterKeys { key ->
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

        this.favourites = favourites
        view?.showFavourites(this.favourites)
    }

    override fun favouriteClicked(index: Int) {
        val item = favourites[index]
        view?.navigateToRouteDetails(item.routeID, item.stopName, item.direction)
    }
}