package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.BaseView
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

        fun loadRouteInfo()

        fun loadRouteDirections()
        fun onDirectionClicked(directionIdx: Int)

        fun loadTimeTable()
        fun onTimeClicked(time: String)
        fun setTimetablePosition(position: Int)

        fun loadTimeline()

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
            itemToScrollTo: Int = -1 //TimeIndices = TimeIndices(-1, -1)
        )

        fun highlightTime(tag: String)
        fun unhighlightTime(tag: String)
    }

    interface TimelineView: BaseView {
        fun attachPresenter(newPresenter: Presenter)

        fun showTimeline(timeline: Timeline)
    }
}