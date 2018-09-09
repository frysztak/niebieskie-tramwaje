package software.orpington.rozkladmpk.routeMap

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.Shape
import software.orpington.rozkladmpk.data.source.ApiClient
import java.util.*


class RouteMapActivity : AppCompatActivity(), OnMapReadyCallback, RouteMapContract.View {

    private lateinit var presenter: RouteMapContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_map)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
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
        updateMap()
    }

    private var shapes: List<Shape>? = null
    override fun displayShapes(shapes: List<Shape>) {
        this.shapes = shapes
        updateMap()
    }

    private fun updateMap() {
        if (!mapReady || shapes == null) return

        val rnd = Random()
        for (shape in shapes!!) {
            val coords = shape.points.map { point ->
                LatLng(point.latitude, point.longitude)
            }

            val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            val polyline = PolylineOptions()
                .color(color)
                .addAll(coords)
            map?.addPolyline(polyline)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(coords.first(), 2.0f))
        }
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
