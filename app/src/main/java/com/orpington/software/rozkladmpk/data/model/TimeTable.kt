package com.orpington.software.rozkladmpk.data.model

import com.google.gson.annotations.SerializedName

data class TimeTableEntry(
    @SerializedName("TripID") val tripID: String,
    @SerializedName("ArrivalTime") val arrivalTime: String,
    @SerializedName("DepartureTime") val departureTime: String
)

data class TimeTable(
    @SerializedName("RouteID") val routeID: String,
    @SerializedName("AtStop") val atStop: String,
    @SerializedName("FromStop") val fromStop: String,
    @SerializedName("ToStop") val toStop: String,
    @SerializedName("Weekdays") val weekdays: List<TimeTableEntry>,
    @SerializedName("Saturdays") val saturdays: List<TimeTableEntry>,
    @SerializedName("Sundays") val sundays: List<TimeTableEntry>
)