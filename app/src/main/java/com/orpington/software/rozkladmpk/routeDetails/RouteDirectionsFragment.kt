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
import com.orpington.software.rozkladmpk.data.model.RouteDirections
import com.orpington.software.rozkladmpk.data.source.RouteDetailsState
import com.orpington.software.rozkladmpk.utils.afterMeasured
import kotlinx.android.synthetic.main.error_view.view.*
import kotlinx.android.synthetic.main.route_directions.*

class RouteDirectionsFragment : Fragment(), RouteDirectionsContract.View {

    private lateinit var adapter: RouteDirectionsAdapter
    private lateinit var presenter: RouteDirectionsContract.Presenter
    private lateinit var state: RouteDetailsState // weakref?

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.route_directions, container, false)
        presenter = RouteDirectionsPresenter(Injection.provideDataSource(), state)
        adapter = RouteDirectionsAdapter(context!!, presenter)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        routeDirections_recyclerview.apply {
            adapter = this@RouteDirectionsFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        errorLayout.tryAgainButton.setOnClickListener {
            presenter.loadRouteDirections()
        }

        presenter.loadRouteDirections()
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.dropView()
    }

    override fun directionSelected(direction: String) {
        val listener = activity as RouteDirectionsEventListener?
        listener?.directionSelected(direction)
    }

    override fun showRouteDirections(routeDirections: RouteDirections, directionIdxToHighlight: Int) {
        adapter.setItems(routeDirections.directions)
        if (directionIdxToHighlight != -1) {
            routeDirections_recyclerview.afterMeasured {
                highlightDirection(directionIdxToHighlight)
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

    fun setState(state: RouteDetailsState): RouteDirectionsFragment {
        this.state = state
        return this
    }

    companion object {
        fun newInstance(): RouteDirectionsFragment {
            return RouteDirectionsFragment()
        }
    }

}