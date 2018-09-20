package software.orpington.rozkladmpk.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import kotlinx.android.synthetic.main.location_map_fragment_layout.*
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.utils.LocationCallbackReference

class LocationMapFragment : Fragment(), OnMapReadyCallback {

    private val initialLocation = LatLng(51.110415, 17.032925) // Rynek
    private val initialZoom = 13f

    interface OnLocationChangedCallback {
        fun locationChanged(latitude: Float, longitude: Float)
    }

    private var locationChangedCallback: OnLocationChangedCallback? = null
    fun setOnLocationChangedCallback(cb: OnLocationChangedCallback) {
        locationChangedCallback = cb
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        v.findViewById<Button>(R.id.messageButton).setOnClickListener {
            currentState.buttonAction()
        }

        State.GrantPermission.buttonAction = {
            runWithPermissions(Manifest.permission.ACCESS_FINE_LOCATION, options = quickPermissionsOption) {
                currentState = State.Idle
            }
        }

        State.LocationIsDisabled.buttonAction = {
            startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            currentState = State.Idle
        }

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        currentState = State.Idle
    }

    private var map: GoogleMap? = null
    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))
    }

    // Location-related mess starts here

    private class State {
        data class Storage(
            val progressBarVisible: Boolean,
            val messageTextId: Int,
            val buttonTextId: Int,
            var buttonAction: () -> Unit
        )

        companion object {
            val Idle = Storage(false, -1, -1, {})
            val Loading = Storage(true, -1, -1, {})
            val GrantPermission = Storage(false, R.id.allowLocating, R.id.okButton, {})
            val GooglePlayError = Storage(false, R.id.googlePlayErrorMessage, -1, {})
            val LocationIsDisabled = Storage(false, R.id.locationIsDisabled, R.id.enableButton, {})
        }
    }

    private var currentState: State.Storage = State.Idle
        set(value) {
            updateState(value)
        }

    private fun updateState(newState: State.Storage) {
        when (newState) {
            State.Idle -> statusBar.visibility = View.GONE
            State.Loading -> progressBar.visibility = View.VISIBLE
            else -> errorMessage.visibility = View.VISIBLE
        }

        if (newState.messageTextId != -1) {
            messageText.text = context?.getString(newState.messageTextId)
        }

        if (newState.buttonTextId != -1) {
            messageButton.text = context?.getString(newState.buttonTextId)
        }
    }

    private fun checkPlayServices(): Boolean {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(context)
        if (result != ConnectionResult.SUCCESS) {
            currentState = State.GooglePlayError
            return false
        }
        return true
    }

    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationCallbackReference: LocationCallbackReference

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
                locationChangedCallback?.locationChanged(loc.latitude.toFloat(), loc.longitude.toFloat())
            }
        }
        locationCallbackReference = LocationCallbackReference(locationCallback)
    }

    private fun isLocationPermissionGranted(): Boolean {
        val permissionLocation = ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            currentState = State.GrantPermission
        }
        return permissionLocation == PackageManager.PERMISSION_GRANTED
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
                    currentState = State.LocationIsDisabled
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
                locationChangedCallback?.locationChanged(loc.latitude.toFloat(), loc.longitude.toFloat())
            }
        }

        locationProvider
            .requestLocationUpdates(locationRequest, locationCallbackReference, Looper.myLooper())
    }

    override fun onStop() {
        super.onStop()

        locationProvider.removeLocationUpdates(locationCallbackReference)
        unregisterLocationSettingListener()
    }

    override fun onResume() {
        super.onResume()

        if (isLocationPermissionGranted()) {
            if (!isLocationEnabled()) {
                currentState = State.LocationIsDisabled
            }
            registerLocationListener()
            registerLocationSettingListener()
        }
    }

}