package com.orpington.software.rozkladmpk.routeVariants

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.Button
import com.kennyc.view.MultiStateView
import com.orpington.software.rozkladmpk.Injection
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.model.RouteVariant
import com.orpington.software.rozkladmpk.routeDetails.RouteDetailsActivity
import com.orpington.software.rozkladmpk.timetable.TimetableActivity
import com.orpington.software.rozkladmpk.utils.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_route_variants.*
import kotlinx.android.synthetic.main.route_variant_bottom_sheet.*
import kotlinx.android.synthetic.main.route_variant_bottom_sheet.view.*


class RouteVariantsActivity : AppCompatActivity(), RouteVariantsContract.View {

    private lateinit var presenter: RouteVariantsPresenter
    private lateinit var recyclerAdapter: RoutesRecyclerViewAdapter
    private lateinit var variantsRecyclerAdapter: VariantsRecyclerViewAdapter

    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_variants)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var stopName = intent.getStringExtra("stopName")

        presenter = RouteVariantsPresenter(Injection.provideDataSource(cacheDir), this)
        recyclerAdapter = RoutesRecyclerViewAdapter(this, presenter)

        var layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerAdapter
        val itemDecoration = GridSpacingItemDecoration(2, 50, true)
        recyclerView.addItemDecoration(itemDecoration)

        variantsRecyclerAdapter = VariantsRecyclerViewAdapter(this, presenter)
        bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheet.variantsRecyclerView.layoutManager = LinearLayoutManager(this)
        bottomSheet.variantsRecyclerView.adapter = variantsRecyclerAdapter

        title = stopName
        presenter.loadVariants(stopName)

        multiStateView.getView(MultiStateView.VIEW_STATE_ERROR)
            ?.findViewById<Button>(R.id.tryAgainButton)
            ?.setOnClickListener {
                presenter.loadVariants(stopName)
            }
    }

    override fun showRoutes(variants: List<RouteVariant>) {
        multiStateView.viewState = MultiStateView.VIEW_STATE_CONTENT
        recyclerAdapter.setItems(variants)
    }

    override fun showVariants(variants: List<RouteVariant>) {
        variantsRecyclerAdapter.setItems(variants)
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun showProgressBar() {
        multiStateView.viewState = MultiStateView.VIEW_STATE_LOADING
    }

    override fun reportThatSomethingWentWrong() {
        multiStateView.viewState = MultiStateView.VIEW_STATE_ERROR
    }

    override fun navigateToTimetable(routeID: String, atStop: String, fromStop: String, toStop: String) {
        val i = Intent(baseContext, TimetableActivity::class.java)
        i.putExtra("routeID", routeID)
        i.putExtra("atStop", atStop)
        i.putExtra("fromStop", fromStop)
        i.putExtra("toStop", toStop)
        startActivity(i)
    }

    override fun navigateToRouteDetails(routeID: String, stopName: String) {
        val i = Intent(baseContext, RouteDetailsActivity::class.java)
        i.putExtra("routeID", routeID)
        i.putExtra("stopName", stopName)
        startActivity(i)
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
