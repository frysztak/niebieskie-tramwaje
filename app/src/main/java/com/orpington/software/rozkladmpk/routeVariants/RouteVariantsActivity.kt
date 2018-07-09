package com.orpington.software.rozkladmpk.routeVariants

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.orpington.software.rozkladmpk.Injection
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.model.RouteVariant
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var stopName = intent.getStringExtra("stopName")

        presenter = RouteVariantsPresenter(Injection.provideDataSource(), this)
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
        val itemDecor = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        bottomSheet.variantsRecyclerView.addItemDecoration(itemDecor)

        title = stopName
        presenter.loadVariants(stopName)
    }

    override fun showRoutes(variants: List<RouteVariant>) {
        recyclerAdapter.setItems(variants)
    }

    override fun showVariants(variants: List<RouteVariant>) {
        variantsRecyclerAdapter.setItems(variants)
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
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
