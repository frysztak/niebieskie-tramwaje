package software.orpington.rozkladmpk.routeMap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_route_map.*
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.MapData
import software.orpington.rozkladmpk.data.model.VehiclePosition
import software.orpington.rozkladmpk.data.model.VehiclePositions
import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.utils.LocationCallbackReference
import software.orpington.rozkladmpk.utils.convertToBitmap
import software.orpington.rozkladmpk.utils.getBitmapDescriptor


class RouteMapActivity : AppCompatActivity(), OnMapReadyCallback, RouteMapContract.View {

    private lateinit var presenter: RouteMapContract.Presenter

    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_map)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // https://issuetracker.google.com/issues/66402372#comment4
        lateinit var mapFragment: SupportMapFragment
        if (savedInstanceState != null) {
            mapFragment = supportFragmentManager.findFragmentByTag("map") as SupportMapFragment
        } else {
            mapFragment = SupportMapFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, mapFragment, "map")
                .commit()
        }
        mapFragment.getMapAsync(this)

        val httpClient = ApiClient.getHttpClient(cacheDir)
        presenter = RouteMapPresenter(Injection.provideDataSource(httpClient))
        presenter.attachView(this)

        val routeID = intent.getStringExtra("routeID")
        val direction = intent.getStringExtra("direction")
        val stopName = intent.getStringExtra("stopName")
        title = "${getString(R.string.route)} $routeID — $stopName"

        retryButton.setOnClickListener {
            loadData(routeID, direction, stopName)
        }

        loadData(routeID, direction, stopName)

        initLocationManager()
        initLocationCallback()
        if (isLocationPermissionGranted()) {
            registerLocationListener()
        }
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

        locationProvider.removeLocationUpdates(locationCallback)
        locationCallback = null

        vehicleLocationHandler.removeCallbacksAndMessages(null)
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        presenter.attachView(this)

        initLocationCallback()
        if (isLocationPermissionGranted()) {
            registerLocationListener()
        }

        val routeID = intent.getStringExtra("routeID")
        updateVehicleLocation(routeID)
    }

    private var mapReady: Boolean = false
    private var map: GoogleMap? = null
    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        mapReady = true

        try {
            map?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style))
        } catch (e: Resources.NotFoundException) {
        }

        map?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            private var contentsView: View? = null

            override fun getInfoWindow(marker: Marker): View? {
                if (marker.title == null) return null

                if (contentsView == null) {
                    contentsView = layoutInflater.inflate(R.layout.map_marker, null)
                    contentsView?.findViewById<ImageView>(R.id.circle)?.visibility = View.GONE
                }

                contentsView!!
                    .findViewById<TextView>(R.id.stopName)
                    ?.text = marker.title

                contentsView!!
                    .findViewById<TextView>(R.id.onDemand)
                    ?.visibility = if (marker.snippet == null) View.GONE else View.VISIBLE

                return contentsView!!
            }

            override fun getInfoContents(marker: Marker): View? {
                return null
            }
        })

        updateMap()
    }

    private var mapData: MapData? = null
    override fun displayMapData(mapData: MapData) {
        this.mapData = mapData
        updateMap()
    }

    private val lineColors = listOf<Int>(
        Color.parseColor("#E91E63"),
        Color.parseColor("#673AB7"),
        Color.parseColor("#2196F3"),
        Color.parseColor("#00BCD4"),
        Color.parseColor("#4CAF50"),
        Color.parseColor("#CDDC39"),
        Color.parseColor("#FFC107"),
        Color.parseColor("#FF5722"),
        Color.parseColor("#9E9E9E")
    )

    private var colorCounter = 0
    private fun getNextColor(): Int {
        val color = lineColors[colorCounter]

        colorCounter = (colorCounter + 1) % lineColors.size

        return color
    }

    private var userMarker: Marker? = null
    private fun updateMap() {
        if (!mapReady || mapData == null) return
        colorCounter = 0

        val boundsBuilder = LatLngBounds.builder()
        for (shape in mapData!!.shapes) {
            val polyline = PolylineOptions()
                .color(getNextColor())

            for (point in shape.points) {
                val coords = LatLng(point.latitude, point.longitude)
                polyline.add(coords)
                boundsBuilder.include(coords)
            }

            map?.addPolyline(polyline)
        }

        val regularIcon = ContextCompat
            .getDrawable(this, R.drawable.map_marker)
            ?.getBitmapDescriptor() ?: return

        val onDemandIcon = ContextCompat
            .getDrawable(this, R.drawable.map_marker_on_demand)
            ?.getBitmapDescriptor() ?: return


        for (stop in mapData!!.stops) {
            val marker = MarkerOptions()
                .position(LatLng(stop.latitude, stop.longitude))

            if (stop.firstOrLast) {
                val specialMarkerView = LayoutInflater.from(this).inflate(R.layout.map_marker, null, false)
                val stopNameTextView = specialMarkerView.findViewById<TextView>(R.id.stopName)
                stopNameTextView.text = stop.stopName

                marker.icon(BitmapDescriptorFactory.fromBitmap(specialMarkerView.convertToBitmap()))
            } else {
                val icon = if (stop.onDemand) onDemandIcon else regularIcon
                val snippet = if (stop.onDemand) "" else null
                marker
                    .title(stop.stopName)
                    .icon(icon)
                    .snippet(snippet)
            }
            map?.addMarker(marker)
        }

        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 60))
    }

    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        progressBar.show()
    }

    override fun hideProgressBar() {
        progressBar.hide()

        statusBar.visibility = View.GONE
    }

    override fun reportError() {
        statusBar.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
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

    private fun checkPlayServices(): Boolean {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(this)
        return result == ConnectionResult.SUCCESS
    }

    private fun initLocationManager() {
        if (!checkPlayServices()) return

        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 2 * 1000L // 2 seconds

        locationProvider = getFusedLocationProviderClient(this)
    }

    private fun initLocationCallback() {
        locationCallback = LocationCallbackReference(object : LocationCallback() {
            override fun onLocationResult(location: LocationResult?) {
                val loc = location?.lastLocation ?: return
                locationChanged(loc)
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun registerLocationListener() {
        if (!isLocationPermissionGranted()) return

        locationProvider.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                locationChanged(loc)
            }
        }

        locationProvider
            .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    private fun isLocationPermissionGranted(): Boolean {
        val permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionLocation == PackageManager.PERMISSION_GRANTED
    }

    private fun locationChanged(location: Location) {
        if (!mapReady) return

        if (userMarker == null) {
            val specialMarkerView = LayoutInflater.from(this).inflate(R.layout.map_marker, null, false)
            val tv = specialMarkerView
                .findViewById<TextView>(R.id.stopName)
            tv.text = getString(R.string.you)
            tv.setTypeface(tv.typeface, Typeface.BOLD)

            specialMarkerView
                .findViewById<ImageView>(R.id.circle)
                .setImageResource(R.drawable.map_marker_user)

            val icon = BitmapDescriptorFactory.fromBitmap(specialMarkerView.convertToBitmap())

            val opt = MarkerOptions()
                .position(LatLng(location.latitude, location.longitude))
                .icon(icon)
            userMarker = map?.addMarker(opt)
        }

        userMarker?.position = LatLng(location.latitude, location.longitude)
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

    private var vehicleMarkers: List<Marker> = emptyList()
    override fun displayVehiclePositions(data: VehiclePositions) {
        if (!mapReady) return
        if (data.isEmpty()) return

        val addNewMarker = { position: VehiclePosition ->
            val specialMarkerView = LayoutInflater.from(this).inflate(R.layout.map_marker, null, false)

            val tv = specialMarkerView
                .findViewById<TextView>(R.id.stopName)
            tv.text = position.name.toUpperCase()
            tv.setTypeface(tv.typeface, Typeface.BOLD)

            specialMarkerView
                .findViewById<ImageView>(R.id.circle)
                .setImageResource(R.drawable.map_marker_vehicle)

            val opt = MarkerOptions()
                .position(LatLng(position.x, position.y))
                .icon(BitmapDescriptorFactory.fromBitmap(specialMarkerView.convertToBitmap()))
            map?.addMarker(opt)!!
        }

        val newMarkers: MutableList<Marker> = arrayListOf()
        // first make sure we have enough markers
        while (data.size > vehicleMarkers.size + newMarkers.size) {
            newMarkers.add(addNewMarker(data.first()))
        }

        vehicleMarkers += newMarkers

        // update markers with new positions
        for ((position, marker) in data zip vehicleMarkers) {
            marker.position = LatLng(position.x, position.y)
        }

        // remove unused markers
        while (vehicleMarkers.size > data.size) {
            vehicleMarkers.last().remove()
            vehicleMarkers = vehicleMarkers.dropLast(1)
        }

    }
}
