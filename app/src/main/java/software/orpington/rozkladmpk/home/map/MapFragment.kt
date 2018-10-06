package software.orpington.rozkladmpk.home.map

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.home_map_layout.*
import software.orpington.rozkladmpk.Injection
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.MapData
import software.orpington.rozkladmpk.data.model.Shape
import software.orpington.rozkladmpk.data.model.StopsAndRoutes
import software.orpington.rozkladmpk.data.model.VehiclePositions
import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.locationmap.LocationMapCallbacks
import software.orpington.rozkladmpk.locationmap.LocationMapFragment
import software.orpington.rozkladmpk.routeDetails.RouteDetailsActivity


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
        return inflater.inflate(R.layout.home_map_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationMapFragment = childFragmentManager.findFragmentById(R.id.map) as LocationMapFragment
        locationMapFragment.setOnLocationMapCallbacks(this)

        locationMapFragment.overrideFAB(fab)
        locationMapFragment.setRetryButtonAction {
            presenter.retryToLoadData()
            locationMapFragment.popDataFailedToLoad()
        }

        mapSheet_header.post {
            val behaviour = BottomSheetBehavior.from(bottomSheet)
            behaviour.peekHeight = mapSheet_header.height
        }

        nearYouRecyclerView.apply {
            adapter = this@MapFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        // workaround for bug on pre-5.0 (https://stackoverflow.com/a/40864288)
        ViewCompat.postOnAnimation(mapCoordinator) {
            ViewCompat.postInvalidateOnAnimation(mapCoordinator)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun onDestroy() {
        for (frag in childFragmentManager.fragments) {
            childFragmentManager
                .beginTransaction()
                .remove(frag)
                .commitAllowingStateLoss()
        }

        super.onDestroy()
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
    override fun drawStops(stops: List<MapData.Stop>) = locationMapFragment.drawStops(stops)
    override fun clearStops() = locationMapFragment.clearStops()
    override fun drawVehicleMarkers(positions: VehiclePositions) = locationMapFragment.drawVehicleMarkers(positions)
    override fun clearVehicleMarkers() = locationMapFragment.clearVehicleMarkers()

    override fun navigateToRouteDetails(
        routeID: String,
        stopName: String,
        direction: String,
        departureTime: String,
        tripID: Int
    ) {
        val intent = Intent(context, RouteDetailsActivity::class.java)
        intent.apply {
            putExtra("routeID", routeID)
            putExtra("stopName", stopName)
            putExtra("direction", direction)
            putExtra("departureTime", departureTime)
            putExtra("tripID", tripID)
        }
        startActivity(intent)
    }

    companion object {
        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }

}