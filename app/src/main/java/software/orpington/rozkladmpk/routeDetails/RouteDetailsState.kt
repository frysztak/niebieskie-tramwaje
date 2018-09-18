package software.orpington.rozkladmpk.routeDetails

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import software.orpington.rozkladmpk.data.model.TimeTable

@Parcelize
data class RouteDetailsState(
    var routeID: String = "",
    var stopName: String = "",
    var isBus: Boolean = false,

    var tripID: Int = -1,
    var routeDirections: List<String> = emptyList(),
    var favouriteDirections: Set<Int> = emptySet(),
    var currentRouteDirection: Int = -1,

    var timetable: TimeTable? = null,
    var currentTimeTag: String = "",

    var currentTimelinePosition: Int = -1
) : Parcelable
