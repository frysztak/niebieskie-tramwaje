package com.orpington.software.rozkladmpk.activity

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.orpington.software.rozkladmpk.*
import com.orpington.software.rozkladmpk.adapter.RouteListItem
import com.orpington.software.rozkladmpk.data.source.Repository
import com.orpington.software.rozkladmpk.data.source.local.LocalDataSource
import com.orpington.software.rozkladmpk.data.source.remote.ApiClient
import com.orpington.software.rozkladmpk.data.source.remote.ApiService
import com.orpington.software.rozkladmpk.data.source.remote.RemoteDataSource
import com.orpington.software.rozkladmpk.presenter.StopsAndRoutesPresenter
import com.orpington.software.rozkladmpk.routeVariants.RouteVariantsActivity
import com.orpington.software.rozkladmpk.view.MainActivityView
import com.orpington.software.rozkladmpk.view.NavigatingView
import com.xwray.groupie.GroupAdapter


class MainActivity : AppCompatActivity(), MainActivityView, NavigatingView {

    private lateinit var presenter: StopsAndRoutesPresenter
    private lateinit var recyclerAdapter: GroupAdapter<com.xwray.groupie.kotlinandroidextensions.ViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var interactor = TransportLinesInteractorImpl(baseContext)
        val apiService = ApiClient.client.create(ApiService::class.java)
        val repository = Repository(RemoteDataSource.getInstance(apiService), LocalDataSource())
        presenter = StopsAndRoutesPresenter(repository, interactor, this)
        recyclerAdapter = GroupAdapter()
        recyclerAdapter.setOnItemClickListener { item, _ ->
            item.id
            if (item is RouteListItem) {
                presenter.onItemClicked(item.name)
            }
        }

        searchView?.queryHint = "Przystanek..."
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                this@MainActivity.queryTextChanged(newText)
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

    override fun queryTextChanged(newText: String?) {
            presenter.onQueryTextChange(newText)
            val section = presenter.getListSection()
                recyclerAdapter.clear()
                recyclerAdapter.add(section)
    }

    override fun navigateToStopActivity(stopName: String) {
        val i = Intent(baseContext, RouteVariantsActivity::class.java)
        i.putExtra("stopName", stopName)
        startActivity(i)
    }

}
