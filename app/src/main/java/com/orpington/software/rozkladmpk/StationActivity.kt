package com.orpington.software.rozkladmpk

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_station.*


class StationActivity : AppCompatActivity(), NavigatingView {
    override fun navigateToStationActivity(stationId: Int) {
        // remove me
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var stationId: Int = intent.getIntExtra("stationId", -1)

        var interactor = TransportLinesInteractorImpl(baseContext)
        var transportLinesPresenter = TransportLinesPresenter(interactor, this)
        val listener = object: RecyclerViewClickListener {
            override fun onClick(view: View, position: Int) {
                transportLinesPresenter.onItemClicked(position)
            }
        }
        var transportLinesAdapter = TransportLineListAdapter(this, transportLinesPresenter, listener)

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = transportLinesAdapter
        val dividerItemDecoration = DividerItemDecoration(
                applicationContext,
                layoutManager.orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        title = interactor.getStationName(stationId)
        transportLinesPresenter.loadLinesForStation(stationId)
        transportLinesAdapter.notifyDataSetChanged()
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
