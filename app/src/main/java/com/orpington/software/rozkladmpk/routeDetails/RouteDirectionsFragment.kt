package com.orpington.software.rozkladmpk.routeDetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.model.RouteDirections

class RouteDirectionsFragment : Fragment(), RouteDetailsContract.DirectionsView {

    private lateinit var adapter: RouteDirectionsAdapter
    private var presenter: RouteDetailsContract.Presenter? = null
    // TODO? clear presenter when fragment dies

    override fun attachPresenter(newPresenter: RouteDetailsContract.Presenter) {
        presenter = newPresenter
        presenter!!.attachDirectionsView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.route_directions, container, false)

        if (activity == null) {
            return view
        }

        adapter = RouteDirectionsAdapter(activity!!, presenter!!)
        view.findViewById<RecyclerView>(R.id.routeDirections_recyclerview)?.apply {
            adapter = this@RouteDirectionsFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        presenter?.loadRouteDirections()

        return view
    }

    override fun showRouteDirections(routeDirections: RouteDirections) {
        adapter.setItems(routeDirections.directions)
    }

    override fun showTimetable(direction: String) {
    }

    override fun highlightDirection(directionIdx: Int) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.routeDirections_recyclerview)
        val viewHolder = recyclerView?.findViewHolderForAdapterPosition(directionIdx) as RouteDirectionsAdapter.ViewHolder?
        viewHolder?.highlight()
    }

    override fun unhighlightDirection(directionIdx: Int) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.routeDirections_recyclerview)
        val viewHolder = recyclerView?.findViewHolderForAdapterPosition(directionIdx) as RouteDirectionsAdapter.ViewHolder?
        viewHolder?.removeHighlight()
    }

    companion object {
        fun newInstance(): RouteDirectionsFragment {
            return RouteDirectionsFragment()
        }
    }

}