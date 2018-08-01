package com.orpington.software.rozkladmpk.routeDetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.model.Timeline

class RouteTimelineFragment : Fragment(), RouteDetailsContract.TimelineView {

    private lateinit var adapter: RouteTimelineAdapter
    private var presenter: RouteDetailsContract.Presenter? = null

    override fun attachPresenter(newPresenter: RouteDetailsContract.Presenter) {
        presenter = newPresenter
        presenter!!.attachTimelineView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.route_timeline, container, false)

        adapter = RouteTimelineAdapter(context!!)
        view.findViewById<RecyclerView>(R.id.timeline_recyclerview)?.apply {
            adapter = this@RouteTimelineFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        return view
    }

    override fun showProgressBar() {
    }

    override fun reportThatSomethingWentWrong() {
    }

    override fun showTimeline(timeline: Timeline) {
        adapter.setItems(timeline.timeline)
    }

    companion object {
        fun newInstance(): RouteTimelineFragment {
            return RouteTimelineFragment()
        }
    }
}