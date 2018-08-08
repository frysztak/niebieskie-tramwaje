package com.orpington.software.rozkladmpk.routeDetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.model.Timeline
import kotlinx.android.synthetic.main.error_view.view.*
import kotlinx.android.synthetic.main.route_timeline.*

class RouteTimelineFragment : Fragment(), RouteDetailsContract.TimelineView {

    private lateinit var adapter: RouteTimelineAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var presenter: RouteDetailsContract.Presenter? = null

    override fun attachPresenter(newPresenter: RouteDetailsContract.Presenter) {
        presenter = newPresenter
        presenter!!.attachTimelineView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.route_timeline, container, false)
    }

    override fun onDestroyView() {
        presenter?.detachTimelineView()
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = RouteTimelineAdapter(context!!)
        layoutManager = LinearLayoutManager(context)

        timeline_recyclerview?.adapter = adapter
        timeline_recyclerview?.layoutManager = layoutManager

        errorLayout.tryAgainButton.setOnClickListener {
            presenter?.loadTimeline()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter?.loadTimeline()
    }

    override fun onPause() {
        super.onPause()
        val position = layoutManager.findFirstVisibleItemPosition()
        presenter?.setTimelinePosition(position)
    }

    private var skeletonScreen: SkeletonScreen? = null
    override fun showProgressBar() {
        selectDirectionAndTime_textview.visibility = View.GONE
        errorLayout.visibility = View.GONE
        timeline_recyclerview.visibility = View.VISIBLE

        skeletonScreen = Skeleton
            .bind(timeline_recyclerview)
            .adapter(adapter)
            .load(R.layout.route_timeline_skeleton_list_item)
            .show()
    }

    override fun hideProgressBar() {
        selectDirectionAndTime_textview.visibility = View.GONE
        errorLayout.visibility = View.GONE
        timeline_recyclerview.visibility = View.VISIBLE

        skeletonScreen?.hide()
    }

    override fun reportThatSomethingWentWrong() {
        selectDirectionAndTime_textview.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        timeline_recyclerview.visibility = View.GONE
    }

    override fun showTimeline(timeline: Timeline, itemToScrollTo: Int) {
        selectDirectionAndTime_textview.visibility = View.GONE
        errorLayout.visibility = View.GONE
        timeline_recyclerview.visibility = View.VISIBLE

        adapter.setItems(timeline.timeline)
        if (itemToScrollTo != -1) {
            layoutManager.scrollToPositionWithOffset(itemToScrollTo, 0)
        }
    }

    companion object {
        fun newInstance(): RouteTimelineFragment {
            return RouteTimelineFragment()
        }
    }
}