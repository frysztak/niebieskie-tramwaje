package com.orpington.software.rozkladmpk.routeDetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.orpington.software.rozkladmpk.Injection
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.model.Timeline
import kotlinx.android.synthetic.main.error_view.view.*
import kotlinx.android.synthetic.main.route_timeline.*

class RouteTimelineFragment : Fragment(), RouteTimelineContract.View {

    private lateinit var adapter: RouteTimelineAdapter
    private lateinit var presenter: RouteTimelineContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.route_timeline, container, false)
        adapter = RouteTimelineAdapter(context!!)
        presenter = RouteTimelinePresenter(Injection.provideDataSource())

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        timeline_recyclerview?.apply {
            adapter = this@RouteTimelineFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        errorLayout.tryAgainButton.setOnClickListener {
            presenter.loadTimeline()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.dropView()
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

    override fun showTimeline(timeline: Timeline) {
        selectDirectionAndTime_textview.visibility = View.GONE
        errorLayout.visibility = View.GONE
        timeline_recyclerview.visibility = View.VISIBLE

        adapter.setItems(timeline.timeline)
    }

    companion object {
        fun newInstance(): RouteTimelineFragment {
            return RouteTimelineFragment()
        }
    }
}