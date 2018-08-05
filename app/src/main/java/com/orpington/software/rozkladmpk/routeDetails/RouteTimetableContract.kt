package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.BaseView

interface RouteTimetableContract {
    interface Presenter {
        fun attachView(view: View)
        fun dropView()

        fun loadTimeTable()
        fun onTimeClicked(time: String)
    }

    interface View : BaseView{
        fun showTimeTable(
            items: List<TimetableViewHelper.ViewItem>,
            timeToScrollInto: TimeIndices = TimeIndices(-1, -1)
        )

        fun highlightTime(tag: String)
        fun unhighlightTime(tag: String)
    }
}