package software.orpington.rozkladmpk.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RouteDirections(
    @Json(name = "RouteID") val routeID: String,
    @Json(name = "Directions") val directions: List<String>
)