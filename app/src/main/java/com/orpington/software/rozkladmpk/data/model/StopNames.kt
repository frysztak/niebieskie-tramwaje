package com.orpington.software.rozkladmpk.data.model

import com.google.gson.annotations.SerializedName

data class StopNames(
    @SerializedName("StopNames") val stopNames: List<String>
)