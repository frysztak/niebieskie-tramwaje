package com.orpington.software.rozkladmpk.data.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class RouteInfo(
    @Json(name = "RouteID") val routeID: String,
    @Json(name = "RouteType") val routeType: String,
    @Json(name = "IsBus") val isBus: Boolean,
    @Json(name = "ValidFrom") val validFrom: String,
    @Json(name = "ValidUntil") val validUntil: String,
    @Json(name = "AgencyName") val agencyName: String,
    @Json(name = "AgencyUrl") val agencyUrl: String,
    @Json(name = "AgencyPhone") val agencyPhone: String
)