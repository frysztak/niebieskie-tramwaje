package com.orpington.software.rozkladmpk.routeDetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.utils.afterMeasured
import kotlinx.android.synthetic.main.error_view.view.*
import kotlinx.android.synthetic.main.route_timetable.*

class RouteTimetableFragment : Fragment(), RouteDetailsContract.TimetableView {

    private lateinit var adapter: RouteTimetableAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var presenter: RouteDetailsContract.Presenter? = null

    private var highlightColor: Int = -1
    private var normalColor: Int = -1

    override fun attachPresenter(newPresenter: RouteDetailsContract.Presenter) {
        presenter = newPresenter
        presenter!!.attachTimetableView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.route_timetable, container, false)

        highlightColor = resources.getColor(R.color.primary_dark, null)
        normalColor = resources.getColor(R.color.primary_text, null)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = RouteTimetableAdapter(context!!, presenter!!)
        layoutManager = LinearLayoutManager(context)

        timetable_recyclerview?.adapter = adapter
        timetable_recyclerview?.layoutManager = layoutManager

        errorLayout.tryAgainButton.setOnClickListener {
            presenter?.loadTimeTable()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter?.loadTimeTable()
    }

    override fun onPause() {
        super.onPause()
        val position = layoutManager.findFirstVisibleItemPosition()
        presenter?.setTimetablePosition(position)
    }

    override fun showTimeTable(
        items: List<TimetableViewHelper.ViewItem>,
        timeToHighlight: String,
        itemToScrollTo: Int
    ) {
        selectDirection_textview.visibility = View.GONE
        errorLayout.visibility = View.GONE
        timetable_recyclerview.visibility = View.VISIBLE

        adapter.setItems(items)
        if (itemToScrollTo != -1) {
            layoutManager.scrollToPositionWithOffset(itemToScrollTo, 0)
        }

        if (timeToHighlight.isNotEmpty()) {
            timetable_recyclerview.afterMeasured {
                highlightTime(timeToHighlight)
            }
        }
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

    override fun highlightTime(tag: String) {
        val textView =
            timetable_recyclerview?.findViewWithTag<TextView>(tag)
        textView?.setTextColor(highlightColor)
    }

    override fun unhighlightTime(tag: String) {
        val textView =
            timetable_recyclerview?.findViewWithTag<TextView>(tag)
        textView?.setTextColor(normalColor)
    }

    companion object {
        fun newInstance(): RouteTimetableFragment {
            return RouteTimetableFragment()
        }
    }

}