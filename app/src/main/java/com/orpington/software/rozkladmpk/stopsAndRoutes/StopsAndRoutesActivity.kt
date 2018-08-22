package com.orpington.software.rozkladmpk.stopsAndRoutes

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.View
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.orpington.software.rozkladmpk.Injection
import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.about.AboutActivity
import com.orpington.software.rozkladmpk.routeVariants.RouteVariantsActivity
import com.orpington.software.rozkladmpk.stopsForRoute.StopsForRouteActivity
import kotlinx.android.synthetic.main.activity_stops_and_routes.*
import kotlinx.android.synthetic.main.error_view.view.*


class StopsAndRoutesActivity : AppCompatActivity(), StopsAndRoutesContract.View {

    private lateinit var presenter: StopsAndRoutesPresenter
    private lateinit var recyclerAdapter: StopsAndRoutesAdapter

    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stops_and_routes)
        setSupportActionBar(findViewById(R.id.toolbar))

        presenter = StopsAndRoutesPresenter(Injection.provideDataSource(cacheDir))
        presenter.attachView(this)
        recyclerAdapter = StopsAndRoutesAdapter(this, presenter)
        presenter.loadStopsAndRoutes()

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        errorLayout.tryAgainButton.setOnClickListener {
            presenter.loadStopsAndRoutes()
        }

        initLocationManager()
    }

    private fun initLocationManager() {
        val UPDATE_INTERVAL = 10 * 1000L // 10 seconds
        val FASTEST_INTERVAL = 2 * 1000L // 2  seconds

        // Create the location request to start receiving updates
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = UPDATE_INTERVAL
        locationRequest.fastestInterval = FASTEST_INTERVAL

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        locationProvider = getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(location: LocationResult?) {
                val loc = location?.lastLocation ?: return
                presenter.locationChanged(loc.latitude, loc.longitude)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()

        runWithPermissions(Manifest.permission.ACCESS_FINE_LOCATION) {
            locationProvider.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    presenter.locationChanged(loc.latitude, loc.longitude)
                }
            }

            locationProvider
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
    }

    override fun onPause() {
        super.onPause()
        locationProvider.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        presenter.detachView()
        skeletonScreen = null
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.stops_and_routes_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.searchHint)
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        presenter.queryTextChanged(newText)
                    }
                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }
            }
        )

        val aboutItem = menu.findItem(R.id.action_about)
        aboutItem.setOnMenuItemClickListener {
            val i = Intent(baseContext, AboutActivity::class.java)
            startActivity(i)
            true
        }

        return true
    }

    override fun displayStopsAndRoutes(data: List<StopOrRoute>) {
        recyclerView.visibility = View.VISIBLE
        notFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE

        recyclerAdapter.setStopsAndRoutes(data)
    }

    override fun displaySearchResults(data: List<StopOrRoute>) {
        recyclerView.visibility = View.VISIBLE
        notFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE

        recyclerAdapter.setSearchResults(data)
    }

    override fun displayNearbyStops(data: List<StopOrRoute>) {
        recyclerView.visibility = View.VISIBLE
        notFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE

        recyclerAdapter.setNearbyStops(data)
    }

    override fun navigateToRouteVariants(stopName: String) {
        val i = Intent(baseContext, RouteVariantsActivity::class.java)
        i.putExtra("stopName", stopName)
        startActivity(i)
    }

    override fun navigateToStopsForRoute(routeID: String) {
        val i = Intent(baseContext, StopsForRouteActivity::class.java)
        i.putExtra("routeID", routeID)
        startActivity(i)
    }

    override fun reportThatSomethingWentWrong() {
        recyclerView.visibility = View.GONE
        notFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
    }

    override fun showStopNotFound() {
        recyclerView.visibility = View.GONE
        notFoundLayout.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }

    private var skeletonScreen: SkeletonScreen? = null
    override fun showProgressBar() {

        recyclerView.visibility = View.VISIBLE
        notFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE

        skeletonScreen = Skeleton.bind(recyclerView)
            .adapter(recyclerAdapter)
            .load(R.layout.stops_and_routes_skeleton_list_item)
            .show()

    }

    override fun hideProgressBar() {
        skeletonScreen?.hide()
    }
}
