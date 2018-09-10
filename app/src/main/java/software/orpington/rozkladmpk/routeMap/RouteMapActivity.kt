package software.orpington.rozkladmpk.routeMap

import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_route_map.*
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.MapData
import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.utils.convertToBitmap
import software.orpington.rozkladmpk.utils.getBitmapDescriptor


class RouteMapActivity : AppCompatActivity(), OnMapReadyCallback, RouteMapContract.View {

    private lateinit var presenter: RouteMapContract.Presenter

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
                .add(R.id.root, mapFragment, "map")
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
    }

    private fun loadData(routeID: String, direction: String, stopName: String) {
        presenter.loadShapes(
            routeID,
            direction,
            stopName
        )
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.detachView()
    }

    private var mapReady: Boolean = false
    private var map: GoogleMap? = null
    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        mapReady = true

        statusBar.bringToFront()
        statusBar.invalidate()
        progressBar.bringToFront()
        progressBar.invalidate()

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

                val textViews = specialMarkerView.findViewById<ConstraintLayout>(R.id.textViews)
                ViewCompat.setTranslationZ(textViews, 1.0f) // translationZ in XML is only for API 21+

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
}
