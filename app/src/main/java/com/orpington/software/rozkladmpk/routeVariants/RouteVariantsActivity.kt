package com.orpington.software.rozkladmpk.routeVariants

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.TransportLinesInteractorImpl
import com.orpington.software.rozkladmpk.adapter.RouteListItem
import com.orpington.software.rozkladmpk.data.source.Repository
import com.orpington.software.rozkladmpk.data.source.local.LocalDataSource
import com.orpington.software.rozkladmpk.data.source.remote.ApiClient
import com.orpington.software.rozkladmpk.data.source.remote.ApiService
import com.orpington.software.rozkladmpk.data.source.remote.RemoteDataSource
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import kotlinx.android.synthetic.main.activity_main.*


class RouteVariantsActivity : AppCompatActivity(), RouteVariantsContract.View {

    private lateinit var presenter: RouteVariantsPresenter
    private lateinit var recyclerAdapter: GroupAdapter<com.xwray.groupie.kotlinandroidextensions.ViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var stopName = intent.getStringExtra("stopName")

        var interactor = TransportLinesInteractorImpl(baseContext)
        val apiService = ApiClient.client.create(ApiService::class.java)
        val repository = Repository(RemoteDataSource.getInstance(apiService), LocalDataSource())
        presenter = RouteVariantsPresenter(repository, interactor, this)
        recyclerAdapter = GroupAdapter()

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerAdapter
        val dividerItemDecoration = DividerItemDecoration(
            applicationContext,
            layoutManager.orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerAdapter.setOnItemClickListener { item, _ ->
            if (item is RouteListItem) {
                //presenter.onItemClicked(item.name)
            }
        }

        title = stopName
        presenter.loadVariants(stopName)
        //async(CommonPool) {
        //    presenter.loadRoutesForStop(stopName)
        //    val groups = presenter.getGroupedRoutes()
        //    withContext(UI) {
        //        recyclerAdapter.addAll(groups)
        //    }
        //}
    }

    override fun showVariants(variants: List<ExpandableGroup>) {
        recyclerAdapter.addAll(variants)
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
