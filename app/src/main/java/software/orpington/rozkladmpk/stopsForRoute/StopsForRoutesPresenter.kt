package software.orpington.rozkladmpk.stopsForRoute

import software.orpington.rozkladmpk.data.model.Stops
import software.orpington.rozkladmpk.data.source.IDataSource
import software.orpington.rozkladmpk.data.source.RemoteDataSource

class StopsForRoutesPresenter(
    private val dataSource: RemoteDataSource
) : StopsForRouteContract.Presenter {

    private var view: StopsForRouteContract.View? = null
    override fun attachView(view: StopsForRouteContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    private var stops = emptyList<String>()
    private var currentRouteID = ""

    override fun loadStops(routeID: String) {
        currentRouteID = routeID
        view?.showProgressBar()
        dataSource.getStopsForRoute(routeID, object : IDataSource.LoadDataCallback<Stops> {
            override fun onDataLoaded(data: Stops) {
                stops = data.stopNames
                view?.hideProgressBar()
                view?.showStops(stops)
            }

            override fun onDataNotAvailable() {
                view?.hideProgressBar()
                view?.reportThatSomethingWentWrong()
            }

        })
    }

    override fun stopClicked(position: Int) {
        val stopName = stops[position]
        view?.navigateToRouteDetails(currentRouteID, stopName)
    }

}