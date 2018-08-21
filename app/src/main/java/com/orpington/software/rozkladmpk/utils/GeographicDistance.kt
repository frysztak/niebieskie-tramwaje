package com.orpington.software.rozkladmpk.utils

data class Location(val latitude: Float, val longitude: Float) {
    operator fun plus(other: Location): Location {
        return Location(
            latitude + other.latitude,
            longitude + other.longitude
        )
    }

    operator fun minus(other: Location): Location {
        return Location(
            latitude - other.latitude,
            longitude - other.longitude
        )
    }
}

class GeographicDistance(
    center: Location,
    radius: Int
) {
    // calculated using http://www.csgnetwork.com/degreelenllavcalc.html and latitude 51
    private val oneMeterInLatitudeDegrees = 1.0 / 111248.23835493479
    private val oneMeterInLongitudeDegrees = 1.0 / 70197.65060613726

    private var northEastLocation: Location
    private var southWestLocation: Location

    init {
        val latitudeOffset = (radius / 2) * oneMeterInLatitudeDegrees
        val longitudeOffset = (radius / 2) * oneMeterInLongitudeDegrees
        val locationOffset = Location(latitudeOffset.toFloat(), longitudeOffset.toFloat())

        northEastLocation = center - locationOffset
        southWestLocation = center + locationOffset
    }


    fun isWithinBounds(location: Location): Boolean {
        if (location.latitude > southWestLocation.latitude
            && location.latitude < northEastLocation.latitude
            && location.longitude > southWestLocation.longitude
            && location.longitude < northEastLocation.longitude) {
            return true
        }

        return false
    }
}