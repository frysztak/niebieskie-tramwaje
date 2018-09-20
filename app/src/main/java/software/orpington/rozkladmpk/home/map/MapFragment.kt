package software.orpington.rozkladmpk.home.map

import android.support.v4.app.Fragment
import com.google.android.gms.maps.SupportMapFragment
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import software.orpington.rozkladmpk.R


class MapFragment : Fragment() {
    private lateinit var presenter: MapPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.home_map_layout, container, false)
        val mMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mMapFragment?.getMapAsync(this)
        return v
    }

}