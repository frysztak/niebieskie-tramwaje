package software.orpington.rozkladmpk.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class MapData(
    @Json(name = "Shapes") val shapes: List<Shape>,
    @Json(name = "Stops") val stops: List<StopsAndRoutes.Stop>
)

@JsonClass(generateAdapter = true)
data class Shape(
    @Json(name = "ShapeID") val shapeID: Int,
    @Json(name = "Points") val points: List<Point>
) {

    @JsonClass(generateAdapter = true)
    data class Point(
        @Json(name = "Latitude") val latitude: Double,
        @Json(name = "Longitude") val longitude: Double
    )
}
