package software.orpington.rozkladmpk.home.map

import software.orpington.rozkladmpk.data.model.Departures
import software.orpington.rozkladmpk.data.model.StopsAndRoutes
import software.orpington.rozkladmpk.data.source.IDataSource
import software.orpington.rozkladmpk.data.source.RemoteDataSource
import software.orpington.rozkladmpk.home.StopsAndRoutesHelper
import software.orpington.rozkladmpk.utils.GeoLocation
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MapPresenter(
    private val remoteDataSource: RemoteDataSource
) : MapContract.Presenter {

    private var view: MapContract.View? = null
    override fun attachView(view: MapContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    private var stopsAndRoutes: StopsAndRoutes = StopsAndRoutes(emptyList(), emptyList())
    override fun loadStops() {
        view?.showProgressBar()
        remoteDataSource.getStopsAndRoutes(object : IDataSource.LoadDataCallback<StopsAndRoutes> {
            override fun onDataLoaded(data: StopsAndRoutes) {
                stopsAndRoutes = data
                view?.hideProgressBar()
            }

            override fun onDataNotAvailable() {
                view?.hideProgressBar()
                view?.reportThatSomethingWentWrong()
            }
        })
    }

    private lateinit var lastUserLocation: GeoLocation
    override fun locationChanged(latitude: Double, longitude: Double) {
        lastUserLocation = GeoLocation.fromDegrees(latitude, longitude)
        val helper = StopsAndRoutesHelper()
        val nearbyStops = helper.filterNearbyStops(stopsAndRoutes.stops, lastUserLocation)

        loadDepartures(nearbyStops.map { stop ->
            stop.stopName
        })
    }

    private fun convertToViewItems(departures: Departures): List<DepartureViewItem> {
        val viewItems = mutableListOf<DepartureViewItem>()
        for (departure in sortDepartures(departures)) {
            val stopID = departure.stop.stopID

            val stopLocation = GeoLocation.fromDegrees(departure.stop.latitude, departure.stop.longitude)
            val earthRadius = 6378.1 * 1000 // in meters
            val distance = stopLocation.distanceTo(lastUserLocation, earthRadius)
            viewItems.add(DepartureHeader(
                departure.stop.stopName,
                departure.stop.stopID,
                distance.toFloat()
            ))

            val addShowMoreButton = !stopsToShowFullyExpanded.contains(stopID)
            val departures = when (addShowMoreButton) {
                true -> departure.departures.take(2)
                false -> departure.departures
            }
            for (departureDetails in departures) {
                val timezone = TimeZone.getTimeZone("Europe/Warsaw")
                val format = SimpleDateFormat("HH:mm")
                format.timeZone = timezone
                val calendar = Calendar.getInstance(timezone)
                val departureTime = format.parse(departureDetails.departureTime)
                val now = Date(
                    departureTime.year,
                    departureTime.month,
                    departureTime.date,
                    calendar.time.hours,
                    calendar.time.minutes
                )
                val diff = departureTime.time - now.time
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)

                val isBus = stopsAndRoutes.routes.find { route ->
                    route.routeID == departureDetails.routeID
                }?.isBus ?: false

                viewItems.add(DepartureDetails(
                    isBus,
                    departureDetails.routeID,
                    departureDetails.direction,
                    minutes.toInt(),
                    departureDetails.departureTime,
                    departureDetails.onDemand
                ))
            }

            if (addShowMoreButton) {
                viewItems.add(DepartureShowMore)
            }
        }

        return viewItems
    }

    private fun sortDepartures(departures: Departures): Departures {
        return departures.sortedBy { departure ->
            val stopLocation = GeoLocation.fromDegrees(departure.stop.latitude, departure.stop.longitude)
            val earthRadius = 6378.1 * 1000 // in meters
            stopLocation.distanceTo(lastUserLocation, earthRadius)
        }
    }

    private fun updateViewItems() {
        viewItems = convertToViewItems(departures)
        view?.showDepartures(viewItems)
    }

    private var viewItems: List<DepartureViewItem> = emptyList()
    private var departures: Departures = emptyList()
    private var lastStopNames: List<String> = emptyList()

    @get:Synchronized @set:Synchronized
    private var isTryingToLoadDepartures: Boolean = false

    @get:Synchronized @set:Synchronized
    private var departuresFailedToLoad: Boolean = false

    override fun loadDepartures(stopNames: List<String>) {
        lastStopNames = stopNames
        if (stopsAndRoutes.stops.isEmpty()) return
        if (isTryingToLoadDepartures || departuresFailedToLoad) return

        isTryingToLoadDepartures = true
        view?.showProgressBar()
        remoteDataSource.getDepartures(stopNames, object : IDataSource.LoadDataCallback<Departures> {
            override fun onDataLoaded(data: Departures) {
                departures = data
                updateViewItems()
                view?.hideProgressBar()
                isTryingToLoadDepartures = false
                departuresFailedToLoad = false
            }

            override fun onDataNotAvailable() {
                view?.hideProgressBar()
                view?.reportThatSomethingWentWrong()
                isTryingToLoadDepartures = false
                departuresFailedToLoad = true
            }
        })
    }

    private var stopsToShowFullyExpanded: MutableList<Int> = mutableListOf()
    override fun onShowMoreClicked(position: Int) {
        val stopID  = viewItems
            .subList(0, position)
            .filterIsInstance<DepartureHeader>()
            .last()
            .stopID
        stopsToShowFullyExpanded.add(stopID)
        updateViewItems()
    }

    override fun retryToLoadData() {
        if (stopsAndRoutes.stops.isEmpty()) {
            loadStops()
        } else if (departures.isEmpty()) {
            departuresFailedToLoad = false
            loadDepartures(lastStopNames)
        }
    }
}