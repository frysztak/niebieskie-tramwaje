package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.data.model.RouteDirections
import com.orpington.software.rozkladmpk.data.model.RouteInfo
import com.orpington.software.rozkladmpk.data.model.Timeline

interface RouteDetailsContract {
    interface Presenter {
        fun attachInfoView(view: InfoView)
        fun attachDirectionsView(view: DirectionsView)
        fun attachTimetableView(view: TimetableView)
        fun attachTimelineView(view: TimelineView)

        fun setRouteID(id: String)
        fun setStopName(name: String)
        fun setDirection(dir: String)

        fun loadRouteInfo()

        fun loadRouteDirections()
        fun onDirectionClicked(directionIdx: Int)

        fun loadTimeTable()
        fun onTimeClicked(time: String)

        fun loadTimeline()
    }

    interface InfoView {
        fun showRouteInfo(routeInfo: RouteInfo)
        fun switchToTimetableTab()
        fun switchToTimelineTab()
    }

    interface DirectionsView {
        fun attachPresenter(newPresenter: Presenter)

        fun showRouteDirections(routeDirections: RouteDirections)

        fun highlightDirection(directionIdx: Int)
        fun unhighlightDirection(directionIdx: Int)
    }

    interface TimetableView {
        fun attachPresenter(newPresenter: Presenter)

        fun showProgressBar()
        fun reportThatSomethingWentWrong()

        fun showTimeTable(
            items: List<TimetableViewHelper.ViewItem>,
            timeToScrollInto: TimeIndices = TimeIndices(-1, -1)
        )

        fun onTimeClicked()
    }

    interface TimelineView {
        fun attachPresenter(newPresenter: Presenter)

        fun showProgressBar()
        fun reportThatSomethingWentWrong()

        fun showTimeline(timeline: Timeline)
    }
}