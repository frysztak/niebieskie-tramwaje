package software.orpington.rozkladmpk.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RouteVariant(
    @Json(name = "RouteID") val routeID: String,
    @Json(name = "IsBus") val isBus: Boolean,
    @Json(name = "FirstStop") val firstStop: String,
    @Json(name = "LastStop") val lastStop: String,
    @Json(name = "TripIDs") val tripIDs: List<Int>
)

@JsonClass(generateAdapter = true)
data class RouteVariants(
    @Json(name = "routeVariants") val routeVariants: List<RouteVariant>
)