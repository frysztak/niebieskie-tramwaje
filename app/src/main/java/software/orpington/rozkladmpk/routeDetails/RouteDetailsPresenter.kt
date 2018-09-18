package software.orpington.rozkladmpk.routeDetails

import software.orpington.rozkladmpk.data.model.RouteDirections
import software.orpington.rozkladmpk.data.model.RouteInfo
import software.orpington.rozkladmpk.data.model.TimeTable
import software.orpington.rozkladmpk.data.model.Timeline
import software.orpington.rozkladmpk.data.source.IDataSource
import software.orpington.rozkladmpk.data.source.RemoteDataSource
import java.util.*

class RouteDetailsPresenter(
    private val dataSource: RemoteDataSource
) : RouteDetailsContract.Presenter {

    private var infoView: RouteDetailsContract.InfoView? = null
    private var directionsView: RouteDetailsContract.DirectionsView? = null
    private var timetableView: RouteDetailsContract.TimetableView? = null
    private var timelineView: RouteDetailsContract.TimelineView? = null

    private var state = RouteDetailsState()

    override fun attachInfoView(view: RouteDetailsContract.InfoView) {
        infoView = view
    }

    override fun detachInfoView() {
        infoView = null
    }

    override fun attachDirectionsView(view: RouteDetailsContract.DirectionsView) {
        directionsView = view
    }

    override fun detachDirectionsView() {
        directionsView = null
    }

    override fun attachTimetableView(view: RouteDetailsContract.TimetableView) {
        timetableView = view
    }

    override fun detachTimetableView() {
        timetableView = null
    }

    override fun attachTimelineView(view: RouteDetailsContract.TimelineView) {
        timelineView = view
    }

    override fun detachTimelineView() {
        timelineView = null
    }

    override fun setRouteID(id: String) {
        state.routeID = id
    }

    override fun setStopName(name: String) {
        state.stopName = name
    }

    private var directionToNavigateTo: String? = null
    override fun setDirection(direction: String) {
        directionToNavigateTo = direction
        if (state.routeDirections.isEmpty()) {
            state.routeDirections = listOf(direction)
            state.currentRouteDirection = 0
        }
        directionsView?.showRouteDirections(
            state.routeDirections,
            state.favouriteDirections,
            state.currentRouteDirection
        )
    }

    override fun loadRouteInfo() {
        infoView?.showProgressBar()
        dataSource.getRouteInfo(state.routeID, object : IDataSource.LoadDataCallback<RouteInfo> {
            override fun onDataLoaded(data: RouteInfo) {
                infoView?.hideProgressBar()
                infoView?.showRouteInfo(data)
                state.isBus = data.isBus
            }

            override fun onDataNotAvailable() {
                infoView?.hideProgressBar()
                infoView?.reportThatSomethingWentWrong()
            }
        })
    }

    override fun loadRouteDirections() {
        directionsView?.showProgressBar()
        dataSource.getRouteDirectionsThroughStop(state.routeID, state.stopName, object : IDataSource.LoadDataCallback<RouteDirections> {
            override fun onDataLoaded(data: RouteDirections) {
                state.routeDirections = data.directions
                if (directionToNavigateTo != null) {
                    state.currentRouteDirection = state.routeDirections.indexOf(directionToNavigateTo!!)
                    directionToNavigateTo = null
                }
                directionsView?.hideProgressBar()
                initialiseFavouriteDirections()
                directionsView?.showRouteDirections(
                    state.routeDirections,
                    state.favouriteDirections,
                    state.currentRouteDirection
                )
            }

            override fun onDataNotAvailable() {
                directionsView?.hideProgressBar()
                directionsView?.reportThatSomethingWentWrong()
            }
        })
    }

    override fun onDirectionClicked(directionIdx: Int) {
        state.currentRouteDirection = directionIdx
        loadTimeTable()

        directionsView?.highlightDirection(directionIdx)
        infoView?.switchToTimetableTab()
    }

    private fun initialiseFavouriteDirections() {
        val favourites = directionsView?.getFavouriteDirections(state.routeID, state.stopName, state.isBus)
            ?: return

        state.favouriteDirections = favourites.map { direction ->
            state.routeDirections.indexOf(direction)
        }.toSet()
    }

    override fun onDirectionFavouriteClicked(directionIdx: Int) {
        if (state.favouriteDirections.contains(directionIdx)) {
            state.favouriteDirections =
                state.favouriteDirections - directionIdx
        } else {
            state.favouriteDirections =
                state.favouriteDirections + directionIdx
        }

        val directionNames = state.routeDirections.filterIndexed { index, _ ->
            state.favouriteDirections.contains(index)
        }.toSet()

        directionsView?.setFavouriteDirections(
            state.routeID,
            state.stopName,
            state.isBus,
            directionNames,
            state.favouriteDirections
        )
    }

    override fun loadTimeTable() {
        if (state.routeID.isEmpty()
            || state.stopName.isEmpty()
            || state.routeDirections.isEmpty()
            || state.currentRouteDirection == -1) {
            return
        }

        timetableView?.showProgressBar()
        val direction = state.routeDirections[state.currentRouteDirection]
        dataSource.getTimeTable(state.routeID, state.stopName, direction,
            object : IDataSource.LoadDataCallback<TimeTable> {
                override fun onDataLoaded(data: TimeTable) {
                    val helper = TimetableViewHelper()
                    state.timetable = data
                    timetableView?.hideProgressBar()

                    val viewItems = helper.processTimeTable(data)
                    val scrollHelper = TimetableScrollHelper(Calendar.getInstance())
                    val timeToScrollTo = scrollHelper.calculateRowToScrollInto(viewItems)

                    timetableView?.showTimeTable(
                        viewItems,
                        state.currentTimeTag,
                        timeToScrollTo
                    )
                }

                override fun onDataNotAvailable() {
                    timetableView?.hideProgressBar()
                    timetableView?.reportThatSomethingWentWrong()
                }
            })
    }

    /// time: PREFIX:HH:MM, e.g.
    /// WE:07:45
    /// SU:08:34
    override fun onTimeClicked(time: String) {
        val prefix = time.take(2)
        val timetableEntries = when (prefix) {
            TimetableViewHelper.DayType.Weekday.prefix ->
                state.timetable?.weekdays
            TimetableViewHelper.DayType.Saturday.prefix ->
                state.timetable?.saturdays
            TimetableViewHelper.DayType.Sunday.prefix ->
                state.timetable?.sundays
            else -> null
        }

        val hhmm = time.drop(3)
        val entry = timetableEntries?.find { entry ->
            entry.departureTime == hhmm
        } ?: return

        state.tripID = entry.tripID
        loadTimeline()
        if (state.currentTimeTag.isNotEmpty()) {
            timetableView?.unhighlightTime(state.currentTimeTag)
        }
        timetableView?.highlightTime(time)
        infoView?.switchToTimelineTab()

        state.currentTimeTag = time
        state.currentTimelinePosition = -1
    }

    override fun setTimelinePosition(position: Int) {
        state.currentTimelinePosition = position
    }

    override fun loadTimeline() {
        if (state.tripID == -1) {
            return
        }

        timelineView?.showProgressBar()
        dataSource.getTripTimeline(state.tripID,
            object : IDataSource.LoadDataCallback<Timeline> {
                override fun onDataLoaded(data: Timeline) {
                    timelineView?.hideProgressBar()

                    // strip prefix from time tag
                    val timeToFind = state.currentTimeTag.substringAfter(":")
                    val itemToHighlight = data.timeline.indexOfFirst { item ->
                        item.departureTime == timeToFind
                    }

                    if (state.currentTimelinePosition == -1) {
                        state.currentTimelinePosition = itemToHighlight
                    }

                    timelineView?.showTimeline(data, itemToHighlight, state.currentTimelinePosition)
                }

                override fun onDataNotAvailable() {
                    timelineView?.hideProgressBar()
                    timelineView?.reportThatSomethingWentWrong()
                }
            })
    }

    override fun mapClicked() {
        val direction = state.routeDirections[state.currentRouteDirection]
        timetableView?.navigateToMap(
            state.routeID,
            direction,
            state.stopName
        )
    }

    override fun getState(): RouteDetailsState {
        return state
    }

    override fun setState(state: RouteDetailsState) {
        this.state = state
    }
}