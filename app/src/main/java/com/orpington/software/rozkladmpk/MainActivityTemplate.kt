package com.orpington.software.rozkladmpk

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import net.grandcentrix.thirtyinch.TiActivity

abstract class MainActivityTemplate : TiActivity<MainPresenter, NavigatingView>(), NavigatingView  {
    override fun providePresenter(): MainPresenter {
        return MainPresenter()
    }

    lateinit var transportLinesPresenter: TransportLinesPresenter
    lateinit var transportLinesAdapter: TransportLineListAdapter
    open val itemClickListener: RecyclerViewClickListener? = null
    open val searchViewTextListener: SearchView.OnQueryTextListener? = null
    abstract fun loadData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var interactor = TransportLinesInteractorImpl(baseContext)
        transportLinesPresenter = TransportLinesPresenter(interactor, this)
        transportLinesAdapter = TransportLineListAdapter(
            this,
            transportLinesPresenter,
            itemClickListener
        )
        searchView?.queryHint = "Przystanek..."
        searchView?.setOnQueryTextListener(searchViewTextListener)

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = transportLinesAdapter
        val dividerItemDecoration = DividerItemDecoration(
                applicationContext,
                layoutManager.orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        loadData()
    }

    override fun navigateToStopActivity(stopName: String) {
        val i = Intent(baseContext, StopActivity::class.java)
        i.putExtra("stopName", stopName)
        startActivity(i)
    }
}