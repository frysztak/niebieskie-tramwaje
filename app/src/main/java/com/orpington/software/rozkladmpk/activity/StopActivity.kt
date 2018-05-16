package com.orpington.software.rozkladmpk.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.TransportLinesInteractorImpl
import com.orpington.software.rozkladmpk.adapter.RecyclerViewClickListener
import com.orpington.software.rozkladmpk.adapter.StopsAndRoutesRecyclerAdapter
import com.orpington.software.rozkladmpk.presenter.SpecificRoutesPresenter
import com.orpington.software.rozkladmpk.view.NavigatingView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.withContext


class StopActivity : AppCompatActivity(), NavigatingView {
    override fun navigateToStopActivity(stationName: String) {
        // remove me
    }

    private lateinit var presenter: SpecificRoutesPresenter
    private lateinit var recyclerAdapter: StopsAndRoutesRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var stopName = intent.getStringExtra("stopName")

        var interactor = TransportLinesInteractorImpl(baseContext)
        presenter = SpecificRoutesPresenter(interactor, this)
        recyclerAdapter = StopsAndRoutesRecyclerAdapter(
            presenter,
            object : RecyclerViewClickListener {
                override fun onClick(view: View, position: Int) {
                    presenter.onItemClicked(position)
                }
            }
        )

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerAdapter
        val dividerItemDecoration = DividerItemDecoration(
                applicationContext,
                layoutManager.orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        title = stopName
        async(CommonPool) {
            presenter.loadRoutesForStop(stopName)
            withContext(UI) {
                recyclerAdapter.notifyDataSetChanged()
            }
        }
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
