package com.orpington.software.rozkladmpk.routeDetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orpington.software.rozkladmpk.R

class RouteTimetableFragment : Fragment(), RouteDetailsContract.TimetableView {

    private lateinit var adapter: RouteTimetableAdapter
    private var presenter: RouteDetailsContract.Presenter? = null

    override fun attachPresenter(newPresenter: RouteDetailsContract.Presenter) {
        presenter = newPresenter
        presenter!!.attachTimetableView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.route_timetable, container, false)

        if (activity == null) {
            return view
        }

        adapter = RouteTimetableAdapter(activity!!)
        view.findViewById<RecyclerView>(R.id.timetable_recyclerview)?.apply {
            adapter = this@RouteTimetableFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        return view
    }

    override fun showTimeTable(items: List<TimetableViewHelper.ViewItem>, timeToScrollInto: TimeIndices) {
        view?.findViewById<TextView>(R.id.selectDirection_textview)?.visibility = View.INVISIBLE
        adapter.setItems(items)
        view?.findViewById<RecyclerView>(R.id.timetable_recyclerview)?.visibility = View.VISIBLE
    }

    override fun showProgressBar() {

    }

    override fun reportThatSomethingWentWrong() {

    }

    override fun onTimeClicked() {

    }

    companion object {
        fun newInstance(): RouteTimetableFragment {
            return RouteTimetableFragment()
        }
    }

}