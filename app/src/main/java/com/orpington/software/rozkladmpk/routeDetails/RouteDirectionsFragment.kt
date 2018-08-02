package com.orpington.software.rozkladmpk.routeDetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
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

        adapter = RouteDirectionsAdapter(context!!, presenter!!)
        view.findViewById<RecyclerView>(R.id.routeDirections_recyclerview)?.apply {
            adapter = this@RouteDirectionsFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }


        view?.findViewById<Button>(R.id.tryAgainButton)?.setOnClickListener {
            presenter?.loadRouteDirections()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        presenter?.loadRouteDirections()
    }

    override fun showRouteDirections(routeDirections: RouteDirections) {
        adapter.setItems(routeDirections.directions)
    }

    private var skeletonScreen: SkeletonScreen? = null
    override fun showProgressBar() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.routeDirections_recyclerview)
        val errorLayout = view?.findViewById<LinearLayout>(R.id.errorLayout)

        recyclerView?.visibility = View.VISIBLE
        errorLayout?.visibility = View.GONE

        skeletonScreen = Skeleton
            .bind(recyclerView)
            .adapter(adapter)
            .load(R.layout.route_directions_skeleton_list_item)
            .count(5)
            .show()
    }

    override fun hideProgressBar() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.routeDirections_recyclerview)
        val errorLayout = view?.findViewById<LinearLayout>(R.id.errorLayout)

        recyclerView?.visibility = View.VISIBLE
        errorLayout?.visibility = View.GONE

        skeletonScreen?.hide()
    }

    override fun reportThatSomethingWentWrong() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.routeDirections_recyclerview)
        val errorLayout = view?.findViewById<LinearLayout>(R.id.errorLayout)

        recyclerView?.visibility = View.GONE
        errorLayout?.visibility = View.VISIBLE
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