package software.orpington.rozkladmpk.stopsForRoute

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.routeDetails.RouteDetailsActivity
import kotlinx.android.synthetic.main.activity_stops_for_route.*
import kotlinx.android.synthetic.main.error_view.view.*

class StopsForRouteActivity : AppCompatActivity(), StopsForRouteContract.View {

    private lateinit var presenter: StopsForRouteContract.Presenter
    private lateinit var adapter: StopsForRoutesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stops_for_route)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val routeID = intent.getStringExtra("routeID")
        title = "${getString(R.string.route)} $routeID"

        val httpClient = ApiClient.getHttpClient(cacheDir)
        presenter = StopsForRoutesPresenter(Injection.provideDataSource(httpClient))
        adapter = StopsForRoutesAdapter(this, presenter)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        presenter.attachView(this)
        presenter.loadStops(routeID)

        errorLayout.tryAgainButton.setOnClickListener {
            presenter.loadStops(routeID)
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        skeletonScreen = null
        super.onDestroy()
    }

    private var skeletonScreen: SkeletonScreen? = null
    override fun showProgressBar() {

        recyclerView.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE

        skeletonScreen = Skeleton.bind(recyclerView)
            .adapter(adapter)
            .load(R.layout.stops_and_routes_skeleton_list_item)
            .show()

    }

    override fun hideProgressBar() {
        skeletonScreen?.hide()
    }


    override fun reportThatSomethingWentWrong() {
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
    }

    override fun showStops(stopNames: List<String>) {
        adapter.setItems(stopNames)
    }

    override fun navigateToRouteDetails(routeID: String, stopName: String) {
        val i = Intent(baseContext, RouteDetailsActivity::class.java)
        i.putExtra("stopName", stopName)
        i.putExtra("routeID", routeID)
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
