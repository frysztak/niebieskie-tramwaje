package software.orpington.rozkladmpk.home

sealed class FavouriteViewModel

data class FavouriteItem(
    val routeID: String,
    val isBus: Boolean,
    val stopName: String,
    val direction: String
) : FavouriteViewModel()

object FavouriteAddNew : FavouriteViewModel()

interface FavouritesContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()

        fun onFavouritesLoaded(preferencesMap: Map<String, *>)
        fun favouriteClicked(index: Int)
    }

    interface View {
        fun showFavourites(data: List<FavouriteViewModel>)
        fun navigateToRouteDetails(routeID: String, stopName: String, direction: String)
        fun focusSearchBar()
    }
}