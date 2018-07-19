package com.orpington.software.rozkladmpk.timetable

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
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

        presenter = TimetablePresenter(Injection.provideDataSource(cacheDir), this)
        adapter = TimetableRecyclerViewAdapter(this, presenter)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        presenter.loadTimeTable(
            intent.getStringExtra("routeID"),
            intent.getStringExtra("atStop"),
            intent.getStringExtra("fromStop"),
            intent.getStringExtra("toStop")
        )
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

}
