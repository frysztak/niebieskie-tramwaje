package software.orpington.rozkladmpk.home.map

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import software.orpington.rozkladmpk.R
import software.orpington.rozkladmpk.data.model.StopsAndRoutes
import software.orpington.rozkladmpk.utils.convertToBitmap

class MarkerHelper {
    companion object {
        fun addStopMarker(googleMap: GoogleMap?, context: Context?, stop: StopsAndRoutes.Stop): Marker? {
            if (googleMap == null || context == null) return null

            val marker = MarkerOptions()
                .position(LatLng(stop.latitude, stop.longitude))

            val specialMarkerView = LayoutInflater.from(context).inflate(R.layout.map_marker, null, false)
            val stopNameTextView = specialMarkerView.findViewById<TextView>(R.id.stopName)
            stopNameTextView.text = stop.stopName

            marker.icon(BitmapDescriptorFactory.fromBitmap(specialMarkerView.convertToBitmap()))
            return googleMap.addMarker(marker)
        }
    }
}