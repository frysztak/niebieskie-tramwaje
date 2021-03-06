package software.orpington.rozkladmpk.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class TimeTableEntry(
    @Json(name = "TripID") val tripID: Int,
    @Json(name = "ArrivalTime") val arrivalTime: String,
    @Json(name = "DepartureTime") val departureTime: String
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class TimeTable(
    @Json(name = "RouteID") val routeID: String,
    @Json(name = "StopName") val stopName: String,
    @Json(name = "Direction") val direction: String,
    @Json(name = "Weekdays") val weekdays: List<TimeTableEntry>,
    @Json(name = "Saturdays") val saturdays: List<TimeTableEntry>,
    @Json(name = "Sundays") val sundays: List<TimeTableEntry>
) : Parcelable