package com.orpington.software.rozkladmpk.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Stops(
    @Json(name = "RouteID") val routeID: String,
    @Json(name = "StopNames") val stopNames: List<String>
)