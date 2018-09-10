package software.orpington.rozkladmpk.routeMap

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
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
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.MapData
import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.utils.convertToBitmap
import java.util.*


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

        presenter.loadShapes(
            intent.getStringExtra("routeID"),
            intent.getStringExtra("direction"),
            intent.getStringExtra("stopName")
        )
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
        colorCounter = 0
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

        val drawable = ContextCompat.getDrawable(this, R.drawable.map_marker)
        val icon = getMarkerIconFromDrawable(drawable!!)
        for (stop in mapData!!.stops) {
            val marker = MarkerOptions()
                .position(LatLng(stop.latitude, stop.longitude))

            if (stop.firstOrLast) {
                val specialMarkerView = LayoutInflater.from(this).inflate(R.layout.map_marker, null, false)
                val stopNameTextView = specialMarkerView.findViewById<TextView>(R.id.stopName)
                stopNameTextView.text = stop.stopName
                ViewCompat.setTranslationZ(stopNameTextView, 1.0f) // translationZ in XML is only for API 21+
                val specialMarkerIcon = specialMarkerView.convertToBitmap()

                marker
                    .icon(BitmapDescriptorFactory.fromBitmap(specialMarkerIcon))
            } else {
                marker
                    .title(stop.stopName)
                    .icon(icon)
            }
            map?.addMarker(marker)
        }

        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 50))
    }

    private fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor {
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
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
