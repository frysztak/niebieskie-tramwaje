package com.orpington.software.rozkladmpk.routeDetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orpington.software.rozkladmpk.R

class RouteTimetableFragment : Fragment(), RouteDetailsContract.TimetableView {

    private lateinit var adapter: RouteTimetableAdapter
    private var presenter: RouteDetailsContract.Presenter? = null

    override fun attachPresenter(newPresenter: RouteDetailsContract.Presenter) {
        presenter = newPresenter
        presenter!!.attachTimetableView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.route_directions, container, false)

        if (activity == null) {
            return view
        }

        adapter = RouteTimetableAdapter(activity!!)
        view.findViewById<RecyclerView>(R.id.routeDirections_recyclerview)?.apply {
            adapter = this@RouteTimetableFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        return view
    }

    override fun showTimeTable(items: List<TimetableViewHelper.ViewItem>, timeToScrollInto: TimeIndices) {
        adapter.setItems(items)
    }

    override fun showProgressBar() {

    }

    override fun reportThatSomethingWentWrong() {

    }

    companion object {
        fun newInstance(): RouteTimetableFragment {
            return RouteTimetableFragment()
        }
    }

}