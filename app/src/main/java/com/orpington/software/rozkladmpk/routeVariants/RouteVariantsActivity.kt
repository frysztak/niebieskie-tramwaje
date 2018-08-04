package com.orpington.software.rozkladmpk.routeVariants

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import android.view.View
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.data.model.RouteVariant
import com.orpington.software.rozkladmpk.routeDetails.RouteDetailsActivity
import com.orpington.software.rozkladmpk.utils.GridSpacingItemDecoration
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_route_variants.*
import kotlinx.android.synthetic.main.error_view.view.*
import javax.inject.Inject


class RouteVariantsActivity : DaggerAppCompatActivity(), RouteVariantsContract.View {

    @Inject
    internal lateinit var presenter: RouteVariantsPresenter
    private lateinit var recyclerAdapter: RoutesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_variants)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var stopName = intent.getStringExtra("stopName")

        recyclerAdapter = RoutesRecyclerViewAdapter(this, presenter)

        var layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager
        val itemDecoration = GridSpacingItemDecoration(2, 50, true)
        recyclerView.addItemDecoration(itemDecoration)

        title = stopName
        presenter.setStopName(stopName)

        errorLayout.tryAgainButton.setOnClickListener {
            presenter.loadVariants()
        }
    }

    override fun onResume() {
        super.onResume()

        presenter.attachView(this)
        presenter.loadVariants()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dropView()
    }

    override fun showRoutes(variants: List<RouteVariant>) {
        recyclerAdapter.setItems(variants)

        recyclerView.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }

    private var skeletonScreen: SkeletonScreen? = null
    override fun showProgressBar() {
        recyclerView.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE

        skeletonScreen = Skeleton.bind(recyclerView)
            .adapter(recyclerAdapter)
            .load(R.layout.route_skeleton_card_view)
            .show()
    }

    override fun hideProgressBar() {
        skeletonScreen?.hide()

        recyclerView.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }

    override fun reportThatSomethingWentWrong() {
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
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
