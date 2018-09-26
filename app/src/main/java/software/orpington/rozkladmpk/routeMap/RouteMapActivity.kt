package software.orpington.rozkladmpk.routeMap

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.MapData
import software.orpington.rozkladmpk.data.model.VehiclePosition
import software.orpington.rozkladmpk.data.model.VehiclePositions
import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.locationmap.LocationMapCallbacks
import software.orpington.rozkladmpk.locationmap.LocationMapFragment
import software.orpington.rozkladmpk.utils.MapColoursHelper
import software.orpington.rozkladmpk.utils.convertToBitmap
import software.orpington.rozkladmpk.utils.getBitmapDescriptor
import kotlin.math.roundToInt


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

    private var map: GoogleMap? = null
    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap

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

    private val coloursHelper = MapColoursHelper()
    private fun updateMap() {
        locationMapFragment.popDataFailedToLoad()

        if (map == null || mapData == null) return
        coloursHelper.resetCounter()

        // TODO: use method from LocationMapFragment
        val boundsBuilder = LatLngBounds.builder()
        for (shape in mapData!!.shapes) {
            val polyline = PolylineOptions()
                .color(coloursHelper.getNextColor())

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

        // can't call
        //map?.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 60))
        // because of https://github.com/googlemaps/android-samples/issues/10

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.12).roundToInt()
        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), width, height, padding))
    }

    override fun showProgressBar() {
        locationMapFragment.showProgressBar()
    }

    override fun hideProgressBar() {
        locationMapFragment.hideProgressBar()
    }

    override fun reportError() {
        locationMapFragment.pushDataFailedToLoad()
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
