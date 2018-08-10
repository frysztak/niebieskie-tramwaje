package com.orpington.software.rozkladmpk.data.model

import com.google.gson.annotations.SerializedName


data class Stops(
    @SerializedName("RouteID") val routeID: String,
    @SerializedName("StopNames") val stopNames: List<String>
)