package com.orpington.software.rozkladmpk.data.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable


@JsonSerializable
data class StopsAndRoutes(
    @Json(name = "Stops") val stops: List<String>,
    @Json(name = "Routes") val routes: List<Route>
) {

    @JsonSerializable
    data class Route(
        @Json(name = "ID") val routeID: String,
        @Json(name = "IsBus") val isBus: Boolean
    )
}