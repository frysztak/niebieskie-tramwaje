package com.orpington.software.rozkladmpk.data.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable


@JsonSerializable
data class Stops(
    @Json(name = "RouteID") val routeID: String,
    @Json(name = "StopNames") val stopNames: List<String>
)