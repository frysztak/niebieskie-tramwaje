package software.orpington.rozkladmpk.routeVariants

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import android.view.View
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.RouteVariant
import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.routeDetails.RouteDetailsActivity
import software.orpington.rozkladmpk.utils.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_route_variants.*
import kotlinx.android.synthetic.main.error_view.view.*


class RouteVariantsActivity : AppCompatActivity(), RouteVariantsContract.View {

    private lateinit var presenter: RouteVariantsPresenter
    private lateinit var recyclerAdapter: RoutesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_variants)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var stopName = intent.getStringExtra("stopName")

        val httpClient = ApiClient.getHttpClient(cacheDir)
        presenter = RouteVariantsPresenter(Injection.provideDataSource(httpClient))
        recyclerAdapter = RoutesRecyclerViewAdapter(this, presenter)

        var layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerAdapter
        val itemDecoration = GridSpacingItemDecoration(2, 50, true)
        recyclerView.addItemDecoration(itemDecoration)

        title = stopName
        presenter.attachView(this)
        presenter.loadVariants(stopName)

        errorLayout.tryAgainButton.setOnClickListener {
            presenter.loadVariants(stopName)
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        progressBarHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun showRoutes(variants: List<RouteVariant>) {
        recyclerAdapter.setItems(variants)

        recyclerView.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }

    private var skeletonScreen: SkeletonScreen? = null
    private val progressBarHandler = Handler()
    override fun showProgressBar() {
        recyclerView.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE

        val runnable = Runnable {
            skeletonScreen = Skeleton.bind(recyclerView)
                .adapter(recyclerAdapter)
                .load(R.layout.route_skeleton_card_view)
                .show()
        }
        progressBarHandler.postDelayed(runnable, 500)
    }

    override fun hideProgressBar() {
        progressBarHandler.removeCallbacksAndMessages(null)
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
