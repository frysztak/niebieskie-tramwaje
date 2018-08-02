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
import kotlinx.android.synthetic.main.error_view.view.*
import kotlinx.android.synthetic.main.route_timetable.*

class RouteTimetableFragment : Fragment(), RouteDetailsContract.TimetableView {

    private lateinit var adapter: RouteTimetableAdapter
    private var presenter: RouteDetailsContract.Presenter? = null

    override fun attachPresenter(newPresenter: RouteDetailsContract.Presenter) {
        presenter = newPresenter
        presenter!!.attachTimetableView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.route_timetable, container, false)
        adapter = RouteTimetableAdapter(context!!, presenter!!)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        timetable_recyclerview?.apply {
            adapter = this@RouteTimetableFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        errorLayout.tryAgainButton.setOnClickListener {
           presenter?.loadTimeTable()
        }

    }

    override fun showTimeTable(items: List<TimetableViewHelper.ViewItem>, timeToScrollInto: TimeIndices) {
        selectDirection_textview.visibility = View.GONE
        errorLayout.visibility = View.GONE
        timetable_recyclerview.visibility = View.VISIBLE

        adapter.setItems(items)
    }

    private var skeletonScreen: SkeletonScreen? = null
    override fun showProgressBar() {
        selectDirection_textview.visibility = View.GONE
        errorLayout.visibility = View.GONE
        timetable_recyclerview.visibility = View.VISIBLE

        skeletonScreen = Skeleton
            .bind(timetable_recyclerview)
            .adapter(adapter)
            .load(R.layout.route_timetable_skeleton_list_item)
            .show()
    }

    override fun hideProgressBar() {
        selectDirection_textview.visibility = View.GONE
        errorLayout.visibility = View.GONE
        timetable_recyclerview.visibility = View.VISIBLE

        skeletonScreen?.hide()
    }

    override fun reportThatSomethingWentWrong() {
        selectDirection_textview.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        timetable_recyclerview.visibility = View.GONE
    }

    override fun onTimeClicked() {

    }

    companion object {
        fun newInstance(): RouteTimetableFragment {
            return RouteTimetableFragment()
        }
    }

}