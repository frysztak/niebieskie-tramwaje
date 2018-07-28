package com.orpington.software.rozkladmpk.stopsAndRoutes

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.View
import android.widget.Button
import com.kennyc.view.MultiStateView
import com.orpington.software.rozkladmpk.Injection
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.routeDetails.RouteDetailsActivity
import com.orpington.software.rozkladmpk.routeVariants.RouteVariantsActivity
import kotlinx.android.synthetic.main.activity_stops_and_routes.*


class StopsAndRoutesActivity : AppCompatActivity(), StopsAndRoutesContract.View {

    private lateinit var presenter: StopsAndRoutesPresenter
    private lateinit var recyclerAdapter: StopsAndRoutesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stops_and_routes)
        setSupportActionBar(findViewById(R.id.toolbar))

        presenter = StopsAndRoutesPresenter(Injection.provideDataSource(cacheDir), this)
        recyclerAdapter = StopsAndRoutesRecyclerViewAdapter(this, presenter)
        presenter.loadStopNames()

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerAdapter

        multiStateView.getView(MultiStateView.VIEW_STATE_ERROR)
            ?.findViewById<Button>(R.id.tryAgainButton)
            ?.setOnClickListener {
                presenter.loadStopNames()
            }
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
        multiStateView.viewState = MultiStateView.VIEW_STATE_CONTENT
        recyclerView.visibility = View.VISIBLE
        notFoundLayout.visibility = View.GONE

        recyclerAdapter.setStops(stops)
    }

    override fun navigateToRouteVariants(stopName: String) {
        val i = Intent(baseContext, RouteVariantsActivity::class.java)
        i.putExtra("stopName", stopName)
        startActivity(i)
    }

    override fun reportThatSomethingWentWrong() {
        multiStateView.viewState = MultiStateView.VIEW_STATE_ERROR
    }

    override fun showProgressBar() {
        multiStateView.viewState = MultiStateView.VIEW_STATE_LOADING
    }

    override fun hideProgressBar() {
        multiStateView.viewState = MultiStateView.VIEW_STATE_CONTENT
    }

    override fun showStopNotFound() {
        notFoundLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }
}
