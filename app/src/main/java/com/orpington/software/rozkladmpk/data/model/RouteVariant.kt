package com.orpington.software.rozkladmpk.data.model

import com.google.gson.annotations.SerializedName

data class RouteVariant(
    @SerializedName("RouteID") val routeID: String,
    @SerializedName("IsBus") val isBus: Boolean,
    @SerializedName("FirstStop") val firstStop: String,
    @SerializedName("LastStop") val lastStop: String,
    @SerializedName("TripIDs") val tripIDs: List<String>
)

data class RouteVariants(
    @SerializedName("routeVariants") val routeVariants: List<RouteVariant>
)