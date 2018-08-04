package com.orpington.software.rozkladmpk.stopsAndRoutes

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.View
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.routeVariants.RouteVariantsActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_stops_and_routes.*
import kotlinx.android.synthetic.main.error_view.view.*
import javax.inject.Inject


class StopsAndRoutesActivity : DaggerAppCompatActivity(), StopsAndRoutesContract.View {

    @Inject
    internal lateinit var presenter: StopsAndRoutesPresenter
    private lateinit var recyclerAdapter: StopsAndRoutesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stops_and_routes)
        setSupportActionBar(findViewById(R.id.toolbar))

        recyclerAdapter = StopsAndRoutesRecyclerViewAdapter(this, presenter)

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        errorLayout.tryAgainButton.setOnClickListener {
            presenter.loadStopNames()
        }
    }

    override fun onResume() {
        super.onResume()

        presenter.attachView(this)
        presenter.loadStopNames()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.dropView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.stops_and_routes_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.searchHint)
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        presenter.queryTextChanged(newText)
                    }
                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }
            }
        )

        return true
    }

    override fun displayStops(stops: List<String>) {
        recyclerView.visibility = View.VISIBLE
        notFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE

        recyclerAdapter.setStops(stops)
    }

    override fun navigateToRouteVariants(stopName: String) {
        val i = Intent(baseContext, RouteVariantsActivity::class.java)
        i.putExtra("stopName", stopName)
        startActivity(i)
    }

    override fun reportThatSomethingWentWrong() {
        recyclerView.visibility = View.GONE
        notFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
    }

    override fun showStopNotFound() {
        recyclerView.visibility = View.GONE
        notFoundLayout.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }

    private var skeletonScreen: SkeletonScreen? = null
    override fun showProgressBar() {

        recyclerView.visibility = View.VISIBLE
        notFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE

        skeletonScreen = Skeleton.bind(recyclerView)
            .adapter(recyclerAdapter)
            .load(R.layout.stops_and_routes_skeleton_list_item)
            .show()
    }

    override fun hideProgressBar() {
        skeletonScreen?.hide()
    }


}
