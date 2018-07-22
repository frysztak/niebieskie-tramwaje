package com.orpington.software.rozkladmpk.timetable

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.TextView
import com.orpington.software.rozkladmpk.Injection
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.utils.afterMeasured
import com.orpington.software.rozkladmpk.utils.forceRippleAnimation
import com.orpington.software.rozkladmpk.utils.whenScrollStateIdle
import kotlinx.android.synthetic.main.activity_timetable.*
import mu.KotlinLogging


class TimetableActivity : AppCompatActivity(), TimetableContract.View {

    private val logger = KotlinLogging.logger {}
    private lateinit var presenter: TimetablePresenter
    private lateinit var adapter: TimetableRecyclerViewAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timetable)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter = TimetablePresenter(Injection.provideDataSource(cacheDir), this)
        adapter = TimetableRecyclerViewAdapter(this, presenter)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
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

    override fun showTimeTable(items: List<TimetablePresenter.ViewItem>, timeToScrollInto: TimeIndices) {
        adapter.setItems(items)

        recyclerView.afterMeasured {
            logger.debug { "RecyclerView ready" }
            if (timeToScrollInto.hourIdx >= 0 && adapter.itemCount > timeToScrollInto.hourIdx) {

                this.whenScrollStateIdle {
                    val vh = this.findViewHolderForAdapterPosition(timeToScrollInto.hourIdx)
                    if (vh is TimetableRecyclerViewAdapter.RowViewHolder && vh.linearLayout.childCount > timeToScrollInto.minuteIdx) {
                        val textView = vh.linearLayout.getChildAt(timeToScrollInto.minuteIdx) as TextView
                        logger.debug { "found textview. forcing ripple animation." }
                        textView.forceRippleAnimation()
                    }
                }

                logger.debug { "smooth scrolling to index $timeToScrollInto.hourIdx" }
                this.smoothScrollToPosition(timeToScrollInto.hourIdx)
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
