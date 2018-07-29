package com.orpington.software.rozkladmpk.data.model

import com.google.gson.annotations.SerializedName


data class RouteDirections(
    @SerializedName("RouteID") val routeID: String,
    @SerializedName("Directions") val directions: List<String>
)