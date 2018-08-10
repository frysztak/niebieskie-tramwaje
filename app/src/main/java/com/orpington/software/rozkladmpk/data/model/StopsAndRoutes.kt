package com.orpington.software.rozkladmpk.data.model

import com.google.gson.annotations.SerializedName


data class StopsAndRoutes(
    @SerializedName("Stops") val stops: List<String>,
    @SerializedName("Routes") val routes: List<Route>
) {

    data class Route(
        @SerializedName("ID") val routeID: String,
        @SerializedName("IsBus") val isBus: Boolean
    )
}