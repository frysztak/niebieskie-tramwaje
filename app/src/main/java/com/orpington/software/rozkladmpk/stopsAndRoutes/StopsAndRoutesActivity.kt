package com.orpington.software.rozkladmpk.stopsAndRoutes

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.View
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.orpington.software.rozkladmpk.Injection
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.about.AboutActivity
import com.orpington.software.rozkladmpk.routeVariants.RouteVariantsActivity
import com.orpington.software.rozkladmpk.stopsForRoute.StopsForRouteActivity
import kotlinx.android.synthetic.main.activity_stops_and_routes.*
import kotlinx.android.synthetic.main.error_view.view.*


class StopsAndRoutesActivity : AppCompatActivity(), StopsAndRoutesContract.View {

    private lateinit var presenter: StopsAndRoutesPresenter
    private lateinit var recyclerAdapter: StopsAndRoutesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stops_and_routes)
        setSupportActionBar(findViewById(R.id.toolbar))

        presenter = StopsAndRoutesPresenter(Injection.provideDataSource(cacheDir))
        presenter.attachView(this)
        recyclerAdapter = StopsAndRoutesAdapter(this, presenter)
        presenter.loadStopsAndRoutes()

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        errorLayout.tryAgainButton.setOnClickListener {
            presenter.loadStopsAndRoutes()
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        skeletonScreen = null
        super.onDestroy()
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

        val aboutItem = menu.findItem(R.id.action_about)
        aboutItem.setOnMenuItemClickListener {
            val i = Intent(baseContext, AboutActivity::class.java)
            startActivity(i)
            true
        }

        return true
    }

    override fun displayStopsAndRoutes(data: List<StopOrRoute>) {
        recyclerView.visibility = View.VISIBLE
        notFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE

        recyclerAdapter.setItems(data)
    }

    override fun navigateToRouteVariants(stopName: String) {
        val i = Intent(baseContext, RouteVariantsActivity::class.java)
        i.putExtra("stopName", stopName)
        startActivity(i)
    }

    override fun navigateToStopsForRoute(routeID: String) {
        val i = Intent(baseContext, StopsForRouteActivity::class.java)
        i.putExtra("routeID", routeID)
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
