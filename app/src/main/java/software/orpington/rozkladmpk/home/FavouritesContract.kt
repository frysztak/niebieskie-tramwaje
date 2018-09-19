package software.orpington.rozkladmpk.home

data class FavouriteItem(
    val routeID: String,
    val isBus: Boolean,
    val stopName: String,
    val direction: String
)

interface FavouritesContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()

        fun onFavouritesLoaded(preferencesMap: Map<String, *>)
        fun favouriteClicked(index: Int)
    }

    interface View {
        fun showFavourites(data: List<FavouriteItem>)
        fun navigateToRouteDetails(routeID: String, stopName: String, direction: String)
    }
}