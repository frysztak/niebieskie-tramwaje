package com.orpington.software.rozkladmpk.data.model

import com.google.gson.annotations.SerializedName

data class RouteInfo(
    @SerializedName("RouteID") val routeID: String,
    @SerializedName("TypeID") val typeID: Int,
    @SerializedName("ValidFrom") val validFrom: String,
    @SerializedName("ValidUntil") val validUntil: String,
    @SerializedName("AgencyName") val agencyName: String,
    @SerializedName("AgencyUrl") val agencyUrl: String,
    @SerializedName("AgencyPhone") val agencyPhone: String
)