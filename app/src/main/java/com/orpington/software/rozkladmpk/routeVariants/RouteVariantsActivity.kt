package com.orpington.software.rozkladmpk.routeVariants

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.model.RouteVariant
import com.orpington.software.rozkladmpk.data.source.Repository
import com.orpington.software.rozkladmpk.data.source.local.LocalDataSource
import com.orpington.software.rozkladmpk.data.source.remote.ApiClient
import com.orpington.software.rozkladmpk.data.source.remote.ApiService
import com.orpington.software.rozkladmpk.data.source.remote.RemoteDataSource
import com.orpington.software.rozkladmpk.utils.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_main.*


class RouteVariantsActivity : AppCompatActivity(), RouteVariantsContract.View {

    private lateinit var presenter: RouteVariantsPresenter
    private lateinit var recyclerAdapter: RouteVariantsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var stopName = intent.getStringExtra("stopName")

        val apiService = ApiClient.client.create(ApiService::class.java)
        val repository = Repository(RemoteDataSource.getInstance(apiService), LocalDataSource())
        presenter = RouteVariantsPresenter(repository, this)
        recyclerAdapter = RouteVariantsRecyclerViewAdapter(this)

        var layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerAdapter
        val itemDecoration = GridSpacingItemDecoration(2, 50, true)
        recyclerView.addItemDecoration(itemDecoration)
        //recyclerAdapter.setOnItemClickListener { item, _ ->
        //    if (item is RouteListItem) {
        //        //presenter.onItemClicked(item.name)
        //    }
        //}

        title = stopName
        presenter.loadVariants(stopName)
    }

    override fun showVariants(variants: List<RouteVariant>) {
        recyclerAdapter.setItems(variants)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }
}