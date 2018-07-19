package com.orpington.software.rozkladmpk.timetable

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.orpington.software.rozkladmpk.Injection
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.model.TimeTable
import kotlinx.android.synthetic.main.activity_timetable.*


class TimetableActivity : AppCompatActivity(), TimetableContract.View {

    private lateinit var presenter: TimetablePresenter
    private lateinit var adapter: TimetableRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timetable)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter = TimetablePresenter(Injection.provideDataSource(cacheDir), this)
        adapter = TimetableRecyclerViewAdapter(this, presenter)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val routeID = intent.getStringExtra("routeID")
        val atStop = intent.getStringExtra("atStop")
        val fromStop = intent.getStringExtra("fromStop")
        val toStop = intent.getStringExtra("toStop")

        presenter.loadTimeTable(
            routeID,
            atStop,
            fromStop,
            toStop
        )

        title = "$fromStop - $toStop"
    }

    override fun hideProgressBar() {

    }

    override fun showProgressBar() {

    }

    override fun reportThatSomethingWentWrong() {

    }

    override fun showTimeTable(timeTable: TimeTable) {
        adapter.setTimeTable(timeTable)
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
