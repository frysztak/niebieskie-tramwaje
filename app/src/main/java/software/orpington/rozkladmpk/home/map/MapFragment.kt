package software.orpington.rozkladmpk.home.map

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.locationmap.LocationMapFragment


class MapFragment : Fragment() {
    private lateinit var presenter: MapPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.home_map_layout, container, false)

        val locationMapFragment = childFragmentManager.findFragmentById(R.id.map) as LocationMapFragment
        val fab = v.findViewById<FloatingActionButton>(R.id.fab)
        locationMapFragment.overrideFAB(fab)

        return v
    }

    companion object {
        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }

}