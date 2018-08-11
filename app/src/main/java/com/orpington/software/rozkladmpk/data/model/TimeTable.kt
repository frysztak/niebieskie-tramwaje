package com.orpington.software.rozkladmpk.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeTableEntry(
    @SerializedName("TripID") val tripID: Int,
    @SerializedName("ArrivalTime") val arrivalTime: String,
    @SerializedName("DepartureTime") val departureTime: String
) : Parcelable

@Parcelize
data class TimeTable(
    @SerializedName("RouteID") val routeID: String,
    @SerializedName("StopName") val stopName: String,
    @SerializedName("Direction") val direction: String,
    @SerializedName("Weekdays") val weekdays: List<TimeTableEntry>,
    @SerializedName("Saturdays") val saturdays: List<TimeTableEntry>,
    @SerializedName("Sundays") val sundays: List<TimeTableEntry>
) : Parcelable