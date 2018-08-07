package com.orpington.software.rozkladmpk.routeDetails

import android.os.Parcelable
import com.orpington.software.rozkladmpk.data.model.TimeTable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RouteDetailsState(
    var routeID: String = "",
    var stopName: String = "",

    var timetable: TimeTable? = null,
    var currentTimeTag: String = "",
    var currentTimetablePosition: Int = -1,

    var tripID: String = "",
    var routeDirections: List<String> = emptyList(),
    var currentRouteDirection: Int = -1
) : Parcelable
