package com.orpington.software.rozkladmpk.data.model

import com.google.gson.annotations.SerializedName

data class StopNames(
    @SerializedName("stopNames") val stopNames: List<String>
)