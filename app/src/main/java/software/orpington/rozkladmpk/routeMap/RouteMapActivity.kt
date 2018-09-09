package software.orpington.rozkladmpk.routeMap

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.MapData
import software.orpington.rozkladmpk.data.model.Shape
import software.orpington.rozkladmpk.data.source.ApiClient
import java.util.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor




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

    private var mapData: MapData?= null
    override fun displayMapData(mapData: MapData) {
        this.mapData = mapData
        updateMap()
    }

    private fun updateMap() {
        if (!mapReady || mapData == null) return

        val rnd = Random()
        for (shape in mapData!!.shapes) {
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

        val drawable = ContextCompat.getDrawable(this, R.drawable.map_marker)
        val icon = getMarkerIconFromDrawable(drawable!!)
        for (stop in mapData!!.stops) {
            val marker = MarkerOptions()
                .position(LatLng(stop.latitude, stop.longitude))
                .title(stop.stopName)
                .icon(icon)
            map?.addMarker(marker)
        }
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
