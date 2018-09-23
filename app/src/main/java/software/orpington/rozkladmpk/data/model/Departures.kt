package software.orpington.rozkladmpk.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

typealias Departures = List<Departure>

@JsonClass(generateAdapter = true)
data class Departure(
    @Json(name = "Stop") val stop: StopsAndRoutes.Stop,
    @Json(name = "Departures") val departures: List<DepartureDetails>
) {

    @JsonClass(generateAdapter = true)
    data class DepartureDetails(
        @Json(name = "TripID") val tripID: Int,
        @Json(name = "DepartureTime") val departureTime: String,
        @Json(name = "OnDemand") val onDemand: Boolean,
        @Json(name = "RouteID") val routeID: String,
        @Json(name = "Direction") val direction: String
    )
}