package software.orpington.rozkladmpk.routeMap

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.android.gms.maps.GoogleMap
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.MapData
import software.orpington.rozkladmpk.data.model.VehiclePositions
import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.locationmap.LocationMapCallbacks
import software.orpington.rozkladmpk.locationmap.LocationMapFragment
import software.orpington.rozkladmpk.utils.MapColoursHelper


class RouteMapActivity : AppCompatActivity(), RouteMapContract.View, LocationMapCallbacks {

    private lateinit var locationMapFragment: LocationMapFragment
    private lateinit var presenter: RouteMapContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_map)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val httpClient = ApiClient.getHttpClient(cacheDir)
        presenter = RouteMapPresenter(Injection.provideDataSource(httpClient))
        presenter.attachView(this)

        val routeID = intent.getStringExtra("routeID")
        val direction = intent.getStringExtra("direction")
        val stopName = intent.getStringExtra("stopName")
        title = "${getString(R.string.route)} $routeID — $stopName"

        locationMapFragment =
            supportFragmentManager.findFragmentById(R.id.locationMapFragment) as LocationMapFragment
        locationMapFragment.setOnLocationMapCallbacks(this)

        locationMapFragment.setRetryButtonAction {
            locationMapFragment.popDataFailedToLoad()
            loadData(routeID, direction, stopName)
        }

        loadData(routeID, direction, stopName)
    }

    private fun loadData(routeID: String, direction: String, stopName: String) {
        presenter.loadShapes(
            routeID,
            direction,
            stopName
        )
    }

    override fun onPause() {
        super.onPause()

        presenter.detachView()
        vehicleLocationHandler.removeCallbacksAndMessages(null)
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        presenter.attachView(this)

        val routeID = intent.getStringExtra("routeID")
        updateVehicleLocation(routeID)
    }

    override fun onDestroy() {
        // there are two fragments: explicitly added SupportMapFragment
        // and some internal Google Play Services one.
        // both need to be removed to avoid memory leaks.
        for (frag in supportFragmentManager.fragments) {
            supportFragmentManager
                .beginTransaction()
                .remove(frag)
                .commitAllowingStateLoss()
        }

        super.onDestroy()
    }

    override fun onLocationChanged(latitude: Double, longitude: Double) {
    }


    private var isMapReady = false
    override fun onMapReady(googleMap: GoogleMap) {
        isMapReady = true
        updateMap()
    }

    private var mapData: MapData? = null
    override fun displayMapData(mapData: MapData) {
        this.mapData = mapData
        updateMap()
    }

    private val coloursHelper = MapColoursHelper()
    private fun updateMap() {
        locationMapFragment.popDataFailedToLoad()

        if (!isMapReady || mapData == null) return
        coloursHelper.releaseAllColours()

        for (shape in mapData!!.shapes) {
            locationMapFragment.drawShape(shape, coloursHelper.getNextColor())
        }

        locationMapFragment.drawStops(mapData!!.stops)
    }

    override fun showProgressBar() = locationMapFragment.showProgressBar()
    override fun hideProgressBar() = locationMapFragment.hideProgressBar()
    override fun reportError() = locationMapFragment.pushDataFailedToLoad()

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private val vehicleLocationHandler = Handler()
    private fun updateVehicleLocation(routeID: String) {
        val runnableCode = object : Runnable {
            override fun run() {
                presenter.updateVehiclePosition(routeID)
                vehicleLocationHandler.postDelayed(this, 1000)
            }
        }

        vehicleLocationHandler.post(runnableCode)
    }

    override fun displayVehiclePositions(data: VehiclePositions) = locationMapFragment.drawVehicleMarkers(data)
}
