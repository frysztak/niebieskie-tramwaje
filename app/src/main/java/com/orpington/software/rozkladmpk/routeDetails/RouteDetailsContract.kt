package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.data.model.RouteDirections
import com.orpington.software.rozkladmpk.data.model.RouteInfo

interface RouteDetailsContract {
    interface Presenter {
        fun attachInfoView(view: InfoView)
        fun attachDirectionsView(view: DirectionsView)
        fun attachTimetableView(view: TimetableView)

        fun setRouteID(id: String)
        fun setStopName(name: String)
        fun setDirection(dir: String)

        fun loadRouteInfo()

        fun loadRouteDirections()
        fun directionClicked(directionIdx: Int)

        fun loadTimeTable()
    }

    interface InfoView {
        fun showRouteInfo(routeInfo: RouteInfo)
        fun switchToTimetableTab()
    }

    interface DirectionsView {
        fun attachPresenter(newPresenter: Presenter)

        fun showRouteDirections(routeDirections: RouteDirections)
        fun showTimetable(direction: String)
    }

    interface TimetableView {
        fun attachPresenter(newPresenter: Presenter)

        fun showProgressBar()
        fun reportThatSomethingWentWrong()

        fun showTimeTable(
            items: List<TimetableViewHelper.ViewItem>,
            timeToScrollInto: TimeIndices = TimeIndices(-1, -1)
        )
    }
}