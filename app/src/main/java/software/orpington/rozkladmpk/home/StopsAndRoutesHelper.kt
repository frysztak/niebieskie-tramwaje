package software.orpington.rozkladmpk.home

import software.orpington.rozkladmpk.data.model.StopsAndRoutes
import software.orpington.rozkladmpk.utils.GeoLocation
import software.orpington.rozkladmpk.utils.sort
import me.xdrop.fuzzywuzzy.FuzzySearch


sealed class StopOrRoute

data class Stop(
    val stopName: String,
    val distance: Float = Float.NaN
) : StopOrRoute()

data class Route(
    val routeID: String,
    val isBus: Boolean
) : StopOrRoute()


// https://stackoverflow.com/a/10831704
fun String.stripAccents(): String {
    val tab00c0 = "AAAAAAACEEEEIIII" +
        "DNOOOOO\u00d7\u00d8UUUUYI\u00df" +
        "aaaaaaaceeeeiiii" +
        "\u00f0nooooo\u00f7\u00f8uuuuy\u00fey" +
        "AaAaAaCcCcCcCcDd" +
        "DdEeEeEeEeEeGgGg" +
        "GgGgHhHhIiIiIiIi" +
        "IiJjJjKkkLlLlLlL" +
        "lLlNnNnNnnNnOoOo" +
        "OoOoRrRrRrSsSsSs" +
        "SsTtTtTtUuUuUuUu" +
        "UuUuWwYyYZzZzZzF"

    val vysl = CharArray(this.length)
    var one: Char
    for (i in 0 until this.length) {
        one = this[i]
        if (one in '\u00c0'..'\u017f') {
            one = tab00c0[one.toInt() - '\u00c0'.toInt()]
        }
        vysl[i] = one
    }
    return String(vysl)
}

class StopsAndRoutesHelper {

    fun convertModel(data: StopsAndRoutes): List<StopOrRoute> {
        val items: MutableList<StopOrRoute> = data.stops.map { stop ->
            Stop(stop.stopName)
        }.distinct().toMutableList()

        items.addAll(
            data.routes.map { route ->
                Route(route.routeID, route.isBus)
            }.sort()
        )

        return items
    }

    fun filterItems(items: List<StopOrRoute>, query: String): List<StopOrRoute> {

        if (query.isEmpty()) {
            return items
        }

        val threshold = 65
        val normalisedQuery = query.stripAccents()

        // handle cases like 'A', 'C' etc which correspond to express bus lines
        if (normalisedQuery.length < 2) {
            val routes = items.filterIsInstance(Route::class.java)
                .filter { route ->
                    route.routeID.startsWith(normalisedQuery, true)
                }
            if (routes.isNotEmpty()) return routes
        }

        // handle every other case
        return items.map { item ->
            val str =
                when (item) {
                    is Stop -> item.stopName
                    is Route -> item.routeID
                }.stripAccents()

            val score =
                if (str.contains(normalisedQuery, true))
                    100
                else
                    FuzzySearch.ratio(normalisedQuery, str)

            Pair(item, score)
        }.filter { pair ->
            pair.second > threshold
        }.sortedByDescending { pair ->
            pair.second //score
        }.map { pair ->
            pair.first
        }
    }

    fun filterNearbyStops(stops: List<StopsAndRoutes.Stop>, location: GeoLocation): List<Stop> {
        val earthRadius = 6378.1 * 1000 // in meters
        val maxDistance = 500.0 // meters

        val bounds = location.boundingCoordinates(maxDistance, earthRadius)

        return stops.map { stop ->
            Pair(stop, GeoLocation.fromDegrees(stop.latitude, stop.longitude))
        }.filter { (stop, stopLocation) ->
            stopLocation.latitudeInRadians >= bounds[0].latitudeInRadians
                && stopLocation.latitudeInRadians <= bounds[1].latitudeInRadians
                && stopLocation.longitudeInRadians >= bounds[0].longitudeInRadians
                && stopLocation.longitudeInRadians <= bounds[1].longitudeInRadians
        }.map { (stop, stopLocation) ->
            val distance = stopLocation.distanceTo(location, earthRadius).toFloat()
            Stop(stop.stopName, distance)
        }.sortedBy { (stop, distance) ->
            distance
        }.distinctBy { (stop, distance) ->
            stop
        }.take(3)
    }
}