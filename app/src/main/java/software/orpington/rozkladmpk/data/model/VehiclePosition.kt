package software.orpington.rozkladmpk.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class VehiclePosition(
    @Json(name = "name") val name: String,
    @Json(name = "type") val type: String,
    @Json(name = "y") val y: Double,
    @Json(name = "x") val x: Double,
    @Json(name = "k") val k: Int
)

typealias VehiclePositions = List<VehiclePosition>
