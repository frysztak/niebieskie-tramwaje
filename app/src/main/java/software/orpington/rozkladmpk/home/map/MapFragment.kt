package software.orpington.rozkladmpk.home.map

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.MapData
import software.orpington.rozkladmpk.data.model.Shape
import software.orpington.rozkladmpk.data.model.StopsAndRoutes
import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.locationmap.LocationMapCallbacks
import software.orpington.rozkladmpk.locationmap.LocationMapFragment


class MapFragment : Fragment(), LocationMapCallbacks, MapContract.View {
    private lateinit var presenter: MapPresenter
    private lateinit var adapter: DeparturesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val httpClient = ApiClient.getHttpClient(context!!.cacheDir)
        presenter = MapPresenter(Injection.provideDataSource(httpClient))
        adapter = DeparturesAdapter(context!!, presenter)

        presenter.loadStops()
    }

    private lateinit var locationMapFragment: LocationMapFragment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.home_map_layout, container, false)

        locationMapFragment = childFragmentManager.findFragmentById(R.id.map) as LocationMapFragment
        locationMapFragment.setOnLocationMapCallbacks(this)

        val fab = v.findViewById<FloatingActionButton>(R.id.fab)
        locationMapFragment.overrideFAB(fab)
        locationMapFragment.setRetryButtonAction {
            presenter.retryToLoadData()
            locationMapFragment.popDataFailedToLoad()
        }

        v.findViewById<RecyclerView>(R.id.nearYouRecyclerView).apply {
            adapter = this@MapFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        return v
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun onLocationChanged(latitude: Double, longitude: Double) {
        presenter.locationChanged(latitude, longitude)
    }

    private var googleMap: GoogleMap? = null
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    override fun showProgressBar() {
        locationMapFragment.showProgressBar()
    }

    override fun hideProgressBar() {
        locationMapFragment.hideProgressBar()
    }

    override fun reportThatSomethingWentWrong() {
        locationMapFragment.pushDataFailedToLoad()
    }

    override fun showDepartures(data: List<DepartureViewItem>) = adapter.setItems(data)

    private var stopMarkers: List<Marker> = emptyList()
    override fun showStopMarkers(stops: List<StopsAndRoutes.Stop>) {
        stopMarkers.forEach { marker ->
            marker.remove()
        }

        stopMarkers = stops.mapNotNull { stop ->
            MarkerHelper.addStopMarker(googleMap, context, stop)
        }
    }

    override fun drawShape(shape: Shape, colour: Int) = locationMapFragment.drawShape(shape, colour)
    override fun clearShapes() = locationMapFragment.clearShapes()

    companion object {
        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }

}