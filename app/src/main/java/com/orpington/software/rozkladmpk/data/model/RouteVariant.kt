package com.orpington.software.rozkladmpk.data.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class RouteVariant(
    @Json(name = "RouteID") val routeID: String,
    @Json(name = "IsBus") val isBus: Boolean,
    @Json(name = "FirstStop") val firstStop: String,
    @Json(name = "LastStop") val lastStop: String,
    @Json(name = "TripIDs") val tripIDs: List<Int>
)

@JsonSerializable
data class RouteVariants(
    @Json(name = "routeVariants") val routeVariants: List<RouteVariant>
)