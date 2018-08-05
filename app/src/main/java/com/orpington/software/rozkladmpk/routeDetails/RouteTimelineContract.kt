package com.orpington.software.rozkladmpk.routeDetails

import com.orpington.software.rozkladmpk.BaseView
import com.orpington.software.rozkladmpk.data.model.Timeline

interface RouteTimelineContract {
    interface Presenter {
        fun attachView(view: View)
        fun dropView()

        fun loadTimeline()
    }

    interface View : BaseView {
        fun showTimeline(timeline: Timeline)
    }
}