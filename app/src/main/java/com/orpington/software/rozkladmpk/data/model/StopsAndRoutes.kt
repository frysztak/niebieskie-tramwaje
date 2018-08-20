package com.orpington.software.rozkladmpk.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class StopsAndRoutes(
    @Json(name = "Stops") val stops: List<Stop>,
    @Json(name = "Routes") val routes: List<Route>
) {

    @JsonClass(generateAdapter = true)
    data class Stop(
        @Json(name = "Name") val stopName: String,
        @Json(name = "Latitude") val latitude: Float,
        @Json(name = "Longitude") val longitude: Float
    )

    @JsonClass(generateAdapter = true)
    data class Route(
        @Json(name = "ID") val routeID: String,
        @Json(name = "IsBus") val isBus: Boolean
    )
}