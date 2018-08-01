package com.orpington.software.rozkladmpk.data.model

import com.google.gson.annotations.SerializedName


data class Timeline(
    @SerializedName("TripID") val tripID: String,
    @SerializedName("Timeline") val timeline: List<TimelineEntry>
) {

    data class TimelineEntry(
        @SerializedName("StopName") val stopName: String,
        @SerializedName("DepartureTime") val departureTime: String
    )
}