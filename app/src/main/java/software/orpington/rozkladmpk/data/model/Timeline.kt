package software.orpington.rozkladmpk.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Timeline(
    @Json(name = "TripID") val tripID: Int,
    @Json(name = "Timeline") val timeline: List<TimelineEntry>
) {

    @JsonClass(generateAdapter = true)
    data class TimelineEntry(
        @Json(name = "StopName") val stopName: String,
        @Json(name = "DepartureTime") val departureTime: String,
        @Json(name = "OnDemand") val onDemand: Boolean = false
    )
}