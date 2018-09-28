package software.orpington.rozkladmpk.home.map

import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import software.orpington.rozkladmpk.data.model.*
import software.orpington.rozkladmpk.data.source.IDataSource
import software.orpington.rozkladmpk.data.source.RemoteDataSource
import software.orpington.rozkladmpk.home.StopsAndRoutesHelper
import software.orpington.rozkladmpk.utils.GeoLocation
import software.orpington.rozkladmpk.utils.MapColoursHelper
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MapPresenter(
    private val remoteDataSource: RemoteDataSource
) : MapContract.Presenter {

    private var view: MapContract.View? = null
    override fun attachView(view: MapContract.View) {
        this.view = view

        val initialDelay = 61L - Calendar.getInstance().get(Calendar.SECOND)
        val rate = 30L
        updateScheduler.scheduleAtFixedRate(updateSchedulerCallback, initialDelay, rate, TimeUnit.SECONDS)
    }

    override fun detachView() {
        this.view = null
    }

    private var lastUpdateTime = 0L
    private val minimalTimeDeltaBetweenUpdates = 30 * 1000 // 30 seconds
    private val updateScheduler = Executors.newSingleThreadScheduledExecutor()
    private val updateSchedulerCallback = Runnable {
        launch(UI) {
            val currentTime = System.currentTimeMillis()
            if (currentTime >= (lastUpdateTime + minimalTimeDeltaBetweenUpdates)) {
                loadDepartures(lastStopNames)
            }

            updateVehicleLocations()
        }
    }

    private fun updateVehicleLocations() {
        val routeIDs = trackedDepartures.values.flatten().map { departureDetails ->
            departureDetails.routeID
        }.distinct()

        if (routeIDs.isEmpty()) return

        remoteDataSource.getVehiclePosition(routeIDs, object : IDataSource.LoadDataCallback<VehiclePositions> {
            override fun onDataLoaded(data: VehiclePositions) {
                view?.drawVehicleMarkers(data)
            }

            override fun onDataNotAvailable() {
            }
        })
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

        if (nearbyStops.isEmpty()) {
            departures = emptyList()
            lastStopNames = emptyList()
            updateViewItems()
            return
        }

        loadDepartures(nearbyStops.map { stop ->
            stop.stopName
        })
    }

    private fun convertToViewItems(departures: Departures): List<DepartureViewItem> {
        val viewItems = mutableListOf<DepartureViewItem>()

        if (departures.isEmpty()) {
            viewItems.add(DepartureNotFound)
            return viewItems
        }

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

            val addShowMoreButton = !fullyExpandedStops.contains(stopID)

            val trackedDeparturesToAdd = trackedDepartures.getOrElse(stopID) { mutableListOf() }
            val departuresToAdd = (trackedDeparturesToAdd + when (addShowMoreButton) {
                true -> departure.departures.take(2)
                false -> departure.departures
            }).distinctBy { departureDetails -> departureDetails.tripID }
                .sortedBy { departureDetails -> departureDetails.departureTime }

            for (departureDetails in departuresToAdd) {
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

                val isTracked = trackedDeparturesToAdd.contains(departureDetails)
                val lineColour = trackedDeparturesColours.getOrElse(departureDetails.tripID) { -1 }

                viewItems.add(DepartureDetails(
                    isBus,
                    departureDetails.routeID,
                    departureDetails.direction,
                    minutes.toInt(),
                    departureDetails.departureTime,
                    departureDetails.onDemand,
                    departureDetails.tripID,
                    isTracked,
                    lineColour
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

    @get:Synchronized
    @set:Synchronized
    private var isTryingToLoadDepartures: Boolean = false

    override fun loadDepartures(stopNames: List<String>) {
        lastStopNames = stopNames
        if (stopNames.isEmpty() || stopsAndRoutes.stops.isEmpty()) return
        if (isTryingToLoadDepartures) return

        isTryingToLoadDepartures = true
        view?.showProgressBar()
        remoteDataSource.getDepartures(stopNames, object : IDataSource.LoadDataCallback<Departures> {
            override fun onDataLoaded(data: Departures) {
                departures = data
                updateViewItems()
                view?.hideProgressBar()
                isTryingToLoadDepartures = false
                lastUpdateTime = System.currentTimeMillis()
            }

            override fun onDataNotAvailable() {
                view?.hideProgressBar()
                view?.reportThatSomethingWentWrong()
                isTryingToLoadDepartures = false
            }
        })
    }

    private var fullyExpandedStops: MutableList<Int> = mutableListOf()
    override fun onShowMoreClicked(position: Int) {
        val stopID = viewItems
            .subList(0, position)
            .filterIsInstance<DepartureHeader>()
            .last()
            .stopID
        fullyExpandedStops.add(stopID)
        updateViewItems()
    }

    override fun retryToLoadData() {
        if (stopsAndRoutes.stops.isEmpty()) {
            loadStops()
        } else if (departures.isEmpty()) {
            loadDepartures(lastStopNames)
        }
    }

    // Key: StopID, Value: self-explanatory, I hope
    private val trackedDepartures: MutableMap<Int, MutableList<Departure.DepartureDetails>> = mutableMapOf()

    // Key: TripID, Value: Colour
    private val trackedDeparturesColours: MutableMap<Int, Int> = mutableMapOf()

    private val coloursHelper = MapColoursHelper()
    override fun onTrackButtonClicked(position: Int) {
        val tripID = viewItems.subList(0, position + 1) // should be okay, headers are always followed by other items
            .filterIsInstance<DepartureDetails>()
            .last()
            .tripID

        val stopID = viewItems.subList(0, position + 1) // should be okay, headers are always followed by other items
            .filterIsInstance<DepartureHeader>()
            .last()
            .stopID

        val departureDetails = departures.find { departure ->
            departure.stop.stopID == stopID
        }?.departures?.find { departureDetails ->
            departureDetails.tripID == tripID
        }

        if (departureDetails == null) {
            // this vehicle already has departed.
            // it's not present in 'departures' list,
            // but it should be present in 'trackedDepartures'
            for ((k, v) in trackedDepartures) {
                val itemToRemove = v.find { departureDetails ->
                    departureDetails.tripID == tripID
                }

                if (itemToRemove != null) {
                    v.remove(itemToRemove)
                    coloursHelper.releaseColour(trackedDeparturesColours[tripID] ?: -1)
                    trackedDeparturesColours.remove(tripID)
                }
            }
        } else {
            if (!trackedDepartures.containsKey(stopID)) {
                trackedDepartures[stopID] = mutableListOf()
            }

            if (trackedDepartures[stopID]!!.contains(departureDetails)) {
                trackedDepartures[stopID]!!.remove(departureDetails)
                coloursHelper.releaseColour(trackedDeparturesColours[tripID] ?: -1)
                trackedDeparturesColours.remove(departureDetails.tripID)
            } else {
                trackedDepartures[stopID]!!.add(departureDetails)
                trackedDeparturesColours[departureDetails.tripID] = coloursHelper.getNextColor()
            }
        }

        updateViewItems()
        updateRouteShapes()
        updateVehicleLocations()
    }

    private fun updateRouteShapes() {
        view?.clearShapes()
        view?.clearStops()
        view?.clearVehicleMarkers()
        view?.showProgressBar()

        for (trackedDeparture in trackedDepartures.toMap()) {
            for (trackedDepartureDetails in trackedDeparture.value) {

                val shapeColour = trackedDeparturesColours.getOrElse(trackedDepartureDetails.tripID) { -1 }
                val cb = object : IDataSource.LoadDataCallback<MapData> {
                    override fun onDataLoaded(data: MapData) {
                        view?.drawShape(data.shapes.first(), shapeColour)
                        view?.drawStops(data.stops)
                        view?.hideProgressBar()
                    }

                    override fun onDataNotAvailable() {
                        view?.reportThatSomethingWentWrong()
                        view?.hideProgressBar()
                    }
                }

                remoteDataSource.getTripMapData(trackedDepartureDetails.tripID, cb)
            }
        }
    }

}