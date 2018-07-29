package com.orpington.software.rozkladmpk.routeDetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orpington.software.rozkladmpk.Injection
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.model.RouteDirections

class RouteDirectionsFragment : Fragment(), RouteDirectionsContract.View {

    private lateinit var adapter: RouteDirectionsAdapter
    private lateinit var presenter: RouteDirectionsPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.route_directions, container, false)

        if (activity == null || activity?.cacheDir == null) {
            return view
        }

        presenter = RouteDirectionsPresenter(Injection.provideDataSource(activity!!.cacheDir), this)
        adapter = RouteDirectionsAdapter(activity!!, presenter)
        view.findViewById<RecyclerView>(R.id.routeDirections_recyclerview)?.apply {
            adapter = this@RouteDirectionsFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        val routeID = arguments?.getString("routeID")
        if (routeID != null) {
            presenter.loadRouteDirections(routeID)
        }

        return view
    }

    override fun showRouteDirections(routeDirections: RouteDirections) {
        adapter.setItems(routeDirections.directions)
    }

    override fun showTimetable(direction: String) {

    }

    companion object {
        fun newInstance(routeID: String): RouteDirectionsFragment {
            val fragment = RouteDirectionsFragment()

            val bundle = Bundle().apply {
                putString("routeID", routeID)
            }
            fragment.arguments = bundle

            return fragment
        }
    }

}