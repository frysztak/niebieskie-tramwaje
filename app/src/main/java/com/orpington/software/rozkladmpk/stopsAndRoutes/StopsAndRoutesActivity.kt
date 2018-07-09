package com.orpington.software.rozkladmpk.stopsAndRoutes

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.SearchView
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.source.ApiClient
import com.orpington.software.rozkladmpk.data.source.ApiService
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import com.orpington.software.rozkladmpk.routeVariants.RouteVariantsActivity
import kotlinx.android.synthetic.main.activity_stops_and_routes.*


class StopsAndRoutesActivity : AppCompatActivity(), StopsAndRoutesContract.View {

    private lateinit var presenter: StopsAndRoutesPresenter
    private lateinit var recyclerAdapter: StopsAndRoutesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stops_and_routes)

        val apiService = ApiClient.client.create(ApiService::class.java)
        val dataSource = RemoteDataSource.getInstance(apiService)
        presenter = StopsAndRoutesPresenter(dataSource, this)
        recyclerAdapter = StopsAndRoutesRecyclerViewAdapter(this, presenter)

        searchView?.queryHint = "Przystanek..."
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    presenter.queryTextChanged(newText)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
        })

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerAdapter
        val dividerItemDecoration = DividerItemDecoration(
            applicationContext,
            layoutManager.orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    override fun displayStops(stops: List<String>) {
        recyclerAdapter.setStops(stops)
    }

    override fun navigateToRouteVariants(stopName: String) {
        val i = Intent(baseContext, RouteVariantsActivity::class.java)
        i.putExtra("stopName", stopName)
        startActivity(i)
    }

}
