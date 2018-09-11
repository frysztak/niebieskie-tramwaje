package software.orpington.rozkladmpk.utils

import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import java.lang.ref.WeakReference


// https://github.com/googlesamples/android-play-location/issues/26#issuecomment-356234500
class LocationCallbackReference(locationCallback: LocationCallback) : LocationCallback() {
    private val locationCallbackRef = WeakReference(locationCallback)

    override fun onLocationResult(locationResult: LocationResult?) {
        super.onLocationResult(locationResult)
        if (locationCallbackRef.get() != null) {
            locationCallbackRef.get()?.onLocationResult(locationResult)
        }
    }
}