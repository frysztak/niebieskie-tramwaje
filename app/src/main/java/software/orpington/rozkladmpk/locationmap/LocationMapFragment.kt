package software.orpington.rozkladmpk.locationmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import kotlinx.android.synthetic.main.location_map_fragment_layout.*
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.MapData
import software.orpington.rozkladmpk.data.model.Shape
import software.orpington.rozkladmpk.utils.LocationCallbackReference
import software.orpington.rozkladmpk.utils.convertToBitmap
import software.orpington.rozkladmpk.utils.getBitmapDescriptor
import kotlin.math.roundToInt

interface LocationMapCallbacks {
    fun onMapReady(googleMap: GoogleMap)
    fun onLocationChanged(latitude: Double, longitude: Double)
}

class LocationMapFragment : Fragment(), OnMapReadyCallback, LocationMapContract.View {

    private val initialLocation = LatLng(51.110415, 17.032925) // Rynek
    private val initialZoom = 13f

    private var locationMapCallbacks: LocationMapCallbacks? = null
    fun setOnLocationMapCallbacks(cb: LocationMapCallbacks) {
        locationMapCallbacks = cb
    }

    fun showProgressBar() = presenter.pushMessage(State.Loading)
    fun hideProgressBar() = presenter.popMessage(State.Loading)
    override fun setMessages(msgs: List<Message>) = adapter.setMessages(msgs)

    private var customFAB: FloatingActionButton? = null
    fun overrideFAB(newFAB: FloatingActionButton) {
        customFAB = newFAB

        myLocationFAB.visibility = View.GONE
        myLocationFAB.setOnClickListener(null)

        newFAB.setOnClickListener {
            centerToUserLocation()
        }

        updateFABVisibility()
    }

    fun pushDataFailedToLoad() = presenter.pushMessage(State.FailedToLoadData, true)
    fun popDataFailedToLoad() = presenter.popMessage(State.FailedToLoadData)

    private lateinit var presenter: LocationMapContract.Presenter
    private lateinit var adapter: MessagesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = LocationMapPresenter()
        adapter = MessagesAdapter(context!!, presenter)

        initLocationManager()
        initLocationCallback()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.location_map_fragment_layout, null, false)

        // https://issuetracker.google.com/issues/66402372#comment4
        lateinit var mapFragment: SupportMapFragment
        if (savedInstanceState != null) {
            mapFragment = childFragmentManager.findFragmentByTag("map") as SupportMapFragment
        } else {
            val opt = GoogleMapOptions().camera(
                CameraPosition.fromLatLngZoom(initialLocation, initialZoom)
            )
            mapFragment = SupportMapFragment.newInstance(opt)
            childFragmentManager
                .beginTransaction()
                .replace(R.id.mapContainer, mapFragment, "map")
                .commit()
        }
        mapFragment.getMapAsync(this)

        v.findViewById<RecyclerView>(R.id.messagesList).apply {
            adapter = this@LocationMapFragment.adapter
            layoutManager = LinearLayoutManager(context!!)
            addItemDecoration(DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL))
        }

        v.findViewById<FloatingActionButton>(R.id.myLocationFAB).setOnClickListener {
            centerToUserLocation()
        }

        initStates()

        return v
    }

    private fun initStates() {
        State.GrantPermission.buttonAction = {
            runWithPermissions(Manifest.permission.ACCESS_FINE_LOCATION, options = quickPermissionsOption) {
                presenter.popMessage(State.GrantPermission)
            }
        }

        State.LocationIsDisabled.buttonAction = {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            presenter.popMessage(State.LocationIsDisabled)
        }
    }

    fun setRetryButtonAction(action: () -> Unit) {
        State.FailedToLoadData.buttonAction = action
    }

    private var map: GoogleMap? = null
    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap == null) return

        map = googleMap
        map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))

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

        locationMapCallbacks?.onMapReady(map!!)
    }

    // Location-related mess starts here

    private class State {
        companion object {
            val Loading = Message(true, -1, -1, {})
            val FailedToLoadData = Message(false, R.string.failed_to_load_map_data, R.string.tryAgain, {})
            val GrantPermission = Message(false, R.string.find_nearby_stops, R.string.ok, {})
            val GooglePlayError = Message(false, R.string.google_play_error_msg, -1, {})
            val LocationIsDisabled = Message(false, R.string.location_is_disabled, R.string.enable, {})
        }
    }

    private fun checkPlayServices(): Boolean {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(context)
        if (result != ConnectionResult.SUCCESS) {
            presenter.pushMessage(State.GooglePlayError)
            return false
        }
        presenter.popMessage(State.GooglePlayError)
        return true
    }

    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationCallbackReference: LocationCallbackReference

    private fun notifyLocationChanged(loc: Location) {
        updateUserMarker(loc)
        updateFABVisibility()
        locationMapCallbacks?.onLocationChanged(loc.latitude, loc.longitude)
    }

    private fun initLocationManager() {
        if (!checkPlayServices()) return

        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 2 * 1000L // 2 seconds
        locationRequest.fastestInterval = 5 * 100L // 0.5 second

        locationProvider = getFusedLocationProviderClient(context!!)
    }

    private fun initLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(location: LocationResult?) {
                val loc = location?.lastLocation ?: return
                notifyLocationChanged(loc)
            }
        }
        locationCallbackReference = LocationCallbackReference(locationCallback)
    }

    private fun isLocationPermissionGranted(): Boolean {
        val permissionLocation = ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            presenter.pushMessage(State.GrantPermission)
            return false
        }
        presenter.popMessage(State.GrantPermission)
        return true
    }

    private fun isLocationEnabled(): Boolean {
        var locationMode = 0

        try {
            locationMode = Settings.Secure.getInt(context?.contentResolver, Settings.Secure.LOCATION_MODE)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF
    }

    private var locationSettingsReceiver: BroadcastReceiver? = null
    private fun registerLocationSettingListener() {
        locationSettingsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action!!.matches("android.location.PROVIDERS_CHANGED".toRegex()) && !isLocationEnabled()) {
                    presenter.pushMessage(State.LocationIsDisabled)
                } else {
                    presenter.popMessage(State.LocationIsDisabled)
                }
            }
        }
        context?.registerReceiver(locationSettingsReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
    }

    private fun unregisterLocationSettingListener() {
        if (locationSettingsReceiver != null) {
            context?.unregisterReceiver(locationSettingsReceiver)
            locationSettingsReceiver = null
        }
    }

    private val quickPermissionsOption = QuickPermissionsOptions(
        handleRationale = true,
        rationaleMethod = { rationaleCallback(it) }
    )

    private fun rationaleCallback(req: QuickPermissionsRequest) {
        AlertDialog.Builder(context!!)
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
                notifyLocationChanged(loc)
            }
        }

        locationProvider
            .requestLocationUpdates(locationRequest, locationCallbackReference, Looper.myLooper())
    }

    private var userMarker: Marker? = null
    private fun updateUserMarker(location: Location) {
        if (userMarker == null) {
            val specialMarkerView = LayoutInflater.from(context).inflate(R.layout.map_marker, null, false)
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

    private fun centerToUserLocation() {
        val marker = userMarker ?: return

        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(marker.position, 14.0f)
        )

    }

    private var drawnShapes: MutableList<Polyline> = mutableListOf()
    override fun drawShape(shape: Shape, colour: Int) {
        val boundsBuilder = LatLngBounds.builder()
        val polylineOptions = PolylineOptions()
            .color(colour)

        for (point in shape.points) {
            val coords = LatLng(point.latitude, point.longitude)
            polylineOptions.add(coords)
            boundsBuilder.include(coords)
        }

        val polyline = map?.addPolyline(polylineOptions) ?: return
        drawnShapes.add(polyline)

        centerToBounds(boundsBuilder.build())
    }

    private fun centerToBounds(bounds: LatLngBounds) {
        // can't call
        //map?.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 60))
        // because of https://github.com/googlemaps/android-samples/issues/10

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.12).roundToInt()
        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding))
    }

    override fun clearShapes() {
        for (shape in drawnShapes) {
            shape.remove()
        }
        drawnShapes.clear()
    }

    private val stopMarkers: MutableList<Marker> = mutableListOf()

    // I'm not happy about this, but it'll have to do.
    // I should probably use RxJava to merge all map data requests
    // and get unique stops
    private val alreadyDrawnStops: MutableList<MapData.Stop> = mutableListOf()
    override fun drawStops(stops: List<MapData.Stop>) {
        val context = context ?: return

        val regularIcon = ContextCompat
            .getDrawable(context, R.drawable.map_marker)
            ?.getBitmapDescriptor() ?: return

        val onDemandIcon = ContextCompat
            .getDrawable(context, R.drawable.map_marker_on_demand)
            ?.getBitmapDescriptor() ?: return

        for (stop in stops - alreadyDrawnStops) {
            val markerOptions = MarkerOptions()
                .position(LatLng(stop.latitude, stop.longitude))

            if (stop.firstOrLast) {
                val specialMarkerView = LayoutInflater.from(context).inflate(R.layout.map_marker, null, false)
                val stopNameTextView = specialMarkerView.findViewById<TextView>(R.id.stopName)
                stopNameTextView.text = stop.stopName

                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(specialMarkerView.convertToBitmap()))
            } else {
                val icon = if (stop.onDemand) onDemandIcon else regularIcon
                val snippet = if (stop.onDemand) "" else null
                markerOptions
                    .title(stop.stopName)
                    .icon(icon)
                    .snippet(snippet)
            }
            val marker = map?.addMarker(markerOptions)
            if (marker != null) stopMarkers.add(marker)
            alreadyDrawnStops.add(stop)
        }
    }

    override fun clearStops() {
        for (marker in stopMarkers) {
            marker.remove()
        }
        stopMarkers.clear()
        alreadyDrawnStops.clear()
    }

    private fun updateFABVisibility() {
        (customFAB
            ?: myLocationFAB).visibility = if (userMarker == null) View.GONE else View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()

        locationProvider.removeLocationUpdates(locationCallbackReference)
        unregisterLocationSettingListener()
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)

        if (isLocationPermissionGranted()) {
            if (!isLocationEnabled()) {
                presenter.pushMessage(State.LocationIsDisabled)
            } else {
                presenter.popMessage(State.LocationIsDisabled)
            }
            registerLocationListener()
            registerLocationSettingListener()
        }
    }

    override fun onDestroy() {
        // there are two fragments: explicitly added SupportMapFragment
        // and some internal Google Play Services one.
        // both need to be removed to avoid memory leaks.
        for (frag in childFragmentManager.fragments) {
            childFragmentManager
                .beginTransaction()
                .remove(frag)
                .commitAllowingStateLoss()
        }

        map?.clear()
        map = null
        customFAB = null
        State.GrantPermission.buttonAction = {}
        State.FailedToLoadData.buttonAction = {}
        State.LocationIsDisabled.buttonAction = {}

        super.onDestroy()
    }

}