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
import com.orpington.software.rozkladmpk.utils.afterMeasured
import kotlinx.android.synthetic.main.error_view.view.*
import kotlinx.android.synthetic.main.route_directions.*

class RouteDirectionsFragment : Fragment(), RouteDetailsContract.DirectionsView {

    private lateinit var adapter: RouteDirectionsAdapter
    private var presenter: RouteDetailsContract.Presenter? = null
    // TODO? clear presenter when fragment dies

    override fun attachPresenter(newPresenter: RouteDetailsContract.Presenter) {
        presenter = newPresenter
        presenter!!.attachDirectionsView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.route_directions, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = RouteDirectionsAdapter(context!!, presenter!!)

        routeDirections_recyclerview.apply {
            adapter = this@RouteDirectionsFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        errorLayout.tryAgainButton.setOnClickListener {
            presenter?.loadRouteDirections()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter?.loadRouteDirections()
    }

    override fun showRouteDirections(routeDirections: List<String>,
                                     idxToHighlight: Int) {
        adapter.setItems(routeDirections)
        if (idxToHighlight != -1) {
            routeDirections_recyclerview.afterMeasured {
                highlightDirection(idxToHighlight)
            }
        }
    }

    private var skeletonScreen: SkeletonScreen? = null
    override fun showProgressBar() {
        routeDirections_recyclerview?.visibility = View.VISIBLE
        errorLayout?.visibility = View.GONE

        skeletonScreen = Skeleton
            .bind(routeDirections_recyclerview)
            .adapter(adapter)
            .load(R.layout.route_directions_skeleton_list_item)
            .count(4)
            .show()
    }

    override fun hideProgressBar() {
        routeDirections_recyclerview?.visibility = View.VISIBLE
        errorLayout?.visibility = View.GONE

        skeletonScreen?.hide()
    }

    override fun reportThatSomethingWentWrong() {
        routeDirections_recyclerview?.visibility = View.GONE
        errorLayout?.visibility = View.VISIBLE
    }

    override fun highlightDirection(directionIdx: Int) {
        val viewHolder =
            routeDirections_recyclerview?.findViewHolderForAdapterPosition(directionIdx) as RouteDirectionsAdapter.ViewHolder?
        viewHolder?.highlight()
    }

    override fun unhighlightDirection(directionIdx: Int) {
        val viewHolder =
            routeDirections_recyclerview?.findViewHolderForAdapterPosition(directionIdx) as RouteDirectionsAdapter.ViewHolder?
        viewHolder?.removeHighlight()
    }

    companion object {
        fun newInstance(): RouteDirectionsFragment {
            return RouteDirectionsFragment()
        }
    }

}