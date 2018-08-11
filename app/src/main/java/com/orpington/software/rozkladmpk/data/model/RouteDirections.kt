package com.orpington.software.rozkladmpk.data.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable


@JsonSerializable
data class RouteDirections(
    @Json(name = "RouteID") val routeID: String,
    @Json(name = "Directions") val directions: List<String>
)