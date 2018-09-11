package software.orpington.rozkladmpk.stopsAndRoutes

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.View
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import kotlinx.android.synthetic.main.activity_stops_and_routes.*
import kotlinx.android.synthetic.main.error_view.view.*
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.about.AboutActivity
import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.routeVariants.RouteVariantsActivity
import software.orpington.rozkladmpk.stopsForRoute.StopsForRouteActivity
import software.orpington.rozkladmpk.utils.InputMethodManagerLeakFix
import software.orpington.rozkladmpk.utils.LocationCallbackReference


class StopsAndRoutesActivity : AppCompatActivity(), StopsAndRoutesContract.View {

    private lateinit var presenter: StopsAndRoutesPresenter
    private lateinit var recyclerAdapter: StopsAndRoutesAdapter

    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationCallbackReference: LocationCallbackReference

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stops_and_routes)
        setSupportActionBar(findViewById(R.id.toolbar))
        InputMethodManagerLeakFix.fixInputMethod(this)

        val httpClient = ApiClient.getHttpClient(cacheDir)
        presenter = StopsAndRoutesPresenter(Injection.provideDataSource(httpClient))
        sharedPreferences = getSharedPreferences("PREF", MODE_PRIVATE)

        presenter.attachView(this)
        recyclerAdapter = StopsAndRoutesAdapter(this, presenter)
        presenter.loadStopsAndRoutes()

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerAdapter

        errorLayout.tryAgainButton.setOnClickListener {
            presenter.loadStopsAndRoutes()
        }

        initLocationManager()
        initLocationCallback()
    }

    private fun checkPlayServices(): Boolean {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(this)
        return result == ConnectionResult.SUCCESS
    }

    private fun initLocationManager() {
        if (!checkPlayServices()) {
            presenter.locationGooglePlayError()
        }

        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10 * 1000L // 10 seconds
        locationRequest.fastestInterval = 2 * 1000L // 2  seconds

        locationProvider = getFusedLocationProviderClient(this)
    }

    private fun initLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(location: LocationResult?) {
                val loc = location?.lastLocation ?: return
                presenter.locationChanged(loc.latitude, loc.longitude)
            }
        }
        locationCallbackReference = LocationCallbackReference(locationCallback)
    }

    private fun isLocationEnabled(): Boolean {
        var locationMode = 0

        try {
            locationMode = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF
    }

    private var locationSettingsReceiver: BroadcastReceiver? = null
    private fun registerLocationSettingListener() {
        locationSettingsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action!!.matches("android.location.PROVIDERS_CHANGED".toRegex())) {
                    presenter.setLocationIsDisabled(!isLocationEnabled())
                }
            }
        }
        registerReceiver(locationSettingsReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
    }

    private fun unregisterLocationSettingListener() {
        if (locationSettingsReceiver != null) {
            unregisterReceiver(locationSettingsReceiver)
            locationSettingsReceiver = null
        }
    }

    private val quickPermissionsOption = QuickPermissionsOptions(
        handleRationale = true,
        rationaleMethod = { rationaleCallback(it) }
    )

    private fun rationaleCallback(req: QuickPermissionsRequest) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_denied))
            .setMessage(getString(R.string.location_rationale))
            .setPositiveButton(getString(R.string.ok)) { dialog, which -> req.proceed() }
            .setNegativeButton(getString(R.string.no)) { dialog, which -> req.cancel() }
            .setCancelable(false)
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun registerLocationListener() = runWithPermissions(Manifest.permission.ACCESS_FINE_LOCATION, options = quickPermissionsOption) {
        locationProvider.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                presenter.locationChanged(loc.latitude, loc.longitude)
            }
        }

        locationProvider
            .requestLocationUpdates(locationRequest, locationCallbackReference, Looper.myLooper())
    }

    override fun onPause() {
        super.onPause()

        locationProvider.removeLocationUpdates(locationCallbackReference)
        unregisterLocationSettingListener()
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()

        if (isLocationPermissionGranted()) {
            presenter.setLocationIsDisabled(!isLocationEnabled())
            registerLocationListener()
            registerLocationSettingListener()
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        progressBarHandler.removeCallbacksAndMessages(null)
        skeletonScreen = null

        super.onDestroy()
    }

    override fun isLocationPermissionGranted(): Boolean {
        val permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionLocation == PackageManager.PERMISSION_GRANTED
    }

    private val neverAskKey = "NEVER_ASK_FOR_LOCATION"
    override fun isNeverAskForLocationSet(): Boolean {
        return sharedPreferences.getBoolean(neverAskKey, false)
    }

    override fun setNeverAskForLocation(value: Boolean) {
        sharedPreferences.edit().putBoolean(neverAskKey, value).apply()
    }

    override fun startLocationTracking() {
        registerLocationListener()
    }

    override fun showLocationSettings() {
        startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    override fun setLocationIsDisabled(isDisabled: Boolean) {
        recyclerAdapter.setLocationIsDisabled(isDisabled)
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

    override fun setSearchResults(data: List<StopOrRoute>) {
        recyclerAdapter.setSearchResults(data)
    }

    override fun setStopsAndRoutes(data: List<StopOrRoute>) {
        recyclerAdapter.setStopsAndRoutes(data)
    }

    override fun setNearbyStops(data: List<StopOrRoute>?) {
        recyclerAdapter.setNearbyStops(data)
    }

    override fun setNearbyStopsGooglePlayError() {
        recyclerAdapter.setNearbyStopsGooglePlayError()
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

    override fun showStopsList() {
        recyclerView.visibility = View.VISIBLE
        notFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
    }

    override fun showStopNotFound() {
        recyclerView.visibility = View.GONE
        notFoundLayout.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }

    private var skeletonScreen: SkeletonScreen? = null
    private val progressBarHandler = Handler()
    override fun showProgressBar() {
        recyclerView.visibility = View.VISIBLE
        notFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE

        val runnable = Runnable {
            skeletonScreen = Skeleton.bind(recyclerView)
                .adapter(recyclerAdapter)
                .load(R.layout.stops_and_routes_skeleton_list_item)
                .show()
        }

        progressBarHandler.postDelayed(runnable, 500)
    }

    override fun hideProgressBar() {
        progressBarHandler.removeCallbacksAndMessages(null)
        skeletonScreen?.hide()
    }
}
