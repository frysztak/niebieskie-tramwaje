package software.orpington.rozkladmpk.routeDetails

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import kotlinx.android.synthetic.main.error_view.view.*
import kotlinx.android.synthetic.main.route_directions.*
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.utils.afterMeasured

class RouteDirectionsFragment : Fragment(), RouteDetailsContract.DirectionsView {

    private lateinit var adapter: RouteDirectionsAdapter
    private var presenter: RouteDetailsContract.Presenter? = null
    private lateinit var sharedPreferences: SharedPreferences
    // TODO? clear presenter when fragment dies

    override fun attachPresenter(newPresenter: RouteDetailsContract.Presenter) {
        presenter = newPresenter
        presenter!!.attachDirectionsView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.route_directions, container, false)
    }

    override fun onDestroyView() {
        presenter?.detachDirectionsView()
        progressBarHandler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = RouteDirectionsAdapter(context!!, presenter!!)
        sharedPreferences = context!!.getSharedPreferences("PREF", Context.MODE_PRIVATE)

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
                                     favouriteDirections: Set<Int>,
                                     idxToHighlight: Int) {
        adapter.setItems(routeDirections)
        adapter.setFavourites(favouriteDirections)
        if (idxToHighlight != -1) {
            routeDirections_recyclerview.afterMeasured {
                highlightDirection(idxToHighlight)
            }
        }
    }

    private var skeletonScreen: SkeletonScreen? = null
    private val progressBarHandler = Handler()
    override fun showProgressBar() {
        routeDirections_recyclerview?.visibility = View.VISIBLE
        errorLayout?.visibility = View.GONE

        val runnable = Runnable {
            skeletonScreen = Skeleton
                .bind(routeDirections_recyclerview)
                .adapter(adapter)
                .load(R.layout.route_directions_skeleton_list_item)
                .count(4)
                .show()
        }
        progressBarHandler.postDelayed(runnable, 500)
    }

    override fun hideProgressBar() {
        routeDirections_recyclerview?.visibility = View.VISIBLE
        errorLayout?.visibility = View.GONE

        progressBarHandler.removeCallbacksAndMessages(null)
        skeletonScreen?.hide()
    }

    override fun reportThatSomethingWentWrong() {
        routeDirections_recyclerview?.visibility = View.GONE
        errorLayout?.visibility = View.VISIBLE
    }

    override fun highlightDirection(directionIdx: Int) {
        adapter.setItemToHighlight(directionIdx)
    }

    private fun getFavouriteKey(routeID: String, stopName: String, isBus: Boolean): String {
        val vehicle = if(isBus) "bus" else "tram"
        return "fav_${routeID}_${stopName}_${vehicle}"
    }

    override fun getFavouriteDirections(routeID: String, stopName: String, isBus: Boolean): Set<String> {
        val key = getFavouriteKey(routeID, stopName, isBus)
        return sharedPreferences.getStringSet(key, setOf())
    }

    override fun setFavouriteDirections(
        routeID: String,
        stopName: String,
        isBus: Boolean,
        favourites: Set<String>,
        favouritesIndices: Set<Int>
    ) {
        val key = getFavouriteKey(routeID, stopName, isBus)
        sharedPreferences
            .edit()
            .putStringSet(key, favourites)
            .apply()
        adapter.setFavourites(favouritesIndices)
    }

    companion object {
        fun newInstance(): RouteDirectionsFragment {
            return RouteDirectionsFragment()
        }
    }

}