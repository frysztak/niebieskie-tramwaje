package software.orpington.rozkladmpk.routeDetails

import software.orpington.rozkladmpk.BaseView
import software.orpington.rozkladmpk.data.model.RouteDirections
import software.orpington.rozkladmpk.data.model.RouteInfo
import software.orpington.rozkladmpk.data.model.Timeline

interface RouteDetailsContract {
    interface Presenter {
        fun attachInfoView(view: InfoView)
        fun detachInfoView()
        fun attachDirectionsView(view: DirectionsView)
        fun detachDirectionsView()
        fun attachTimetableView(view: TimetableView)
        fun detachTimetableView()
        fun attachTimelineView(view: TimelineView)
        fun detachTimelineView()

        fun setRouteID(id: String)
        fun setStopName(name: String)

        fun loadRouteInfo()

        fun loadRouteDirections()
        fun onDirectionClicked(directionIdx: Int)

        fun loadTimeTable()
        fun onTimeClicked(time: String)

        fun loadTimeline()
        fun setTimelinePosition(position: Int)

        fun getState(): RouteDetailsState
        fun setState(state: RouteDetailsState)
    }

    interface InfoView : BaseView {
        fun showRouteInfo(routeInfo: RouteInfo)
        fun switchToTimetableTab()
        fun switchToTimelineTab()
    }

    interface DirectionsView : BaseView {
        fun attachPresenter(newPresenter: Presenter)

        fun showRouteDirections(routeDirections: List<String>,
                                idxToHighlight: Int = -1)

        fun highlightDirection(directionIdx: Int)
    }

    interface TimetableView : BaseView {
        fun attachPresenter(newPresenter: Presenter)

        fun showTimeTable(
            items: List<TimetableViewHelper.ViewItem>,
            timeToHighlight: String = "",
            hourToScrollTo: HourCoordinates? = null
        )

        fun highlightTime(tag: String)
        fun unhighlightTime(tag: String)
    }

    interface TimelineView: BaseView {
        fun attachPresenter(newPresenter: Presenter)

        fun showTimeline(
            timeline: Timeline,
            itemToHighlight: Int = -1,
            itemToScrollTo: Int = -1
        )
    }
}