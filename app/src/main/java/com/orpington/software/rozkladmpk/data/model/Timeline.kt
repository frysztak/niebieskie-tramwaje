package com.orpington.software.rozkladmpk.data.model

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable


@JsonSerializable
data class Timeline(
    @Json(name = "TripID") val tripID: Int,
    @Json(name = "Timeline") val timeline: List<TimelineEntry>
) {

    @JsonSerializable
    data class TimelineEntry(
        @Json(name = "StopName") val stopName: String,
        @Json(name = "DepartureTime") val departureTime: String
    )
}