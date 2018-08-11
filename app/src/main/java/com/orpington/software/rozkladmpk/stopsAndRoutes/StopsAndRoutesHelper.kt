package com.orpington.software.rozkladmpk.stopsAndRoutes

import com.orpington.software.rozkladmpk.data.model.StopsAndRoutes
import me.xdrop.fuzzywuzzy.FuzzySearch


sealed class StopOrRoute

data class Stop(
    val stopName: String
) : StopOrRoute()

data class Route(
    val routeID: String,
    val isBus: Boolean
) : StopOrRoute()


class StopsAndRoutesHelper {

    fun <T : List<Route>> T.sort(): List<Route> {
        val comp = Comparator<Route> { r1, r2 ->
            val id1 = r1.routeID.toIntOrNull()
            val id2 = r2.routeID.toIntOrNull()

            if (id1 != null && id2 != null) {
                id1 - id2
            } else {
                r1.routeID.compareTo(r2.routeID)
            }
        }
        return this.sortedWith(comp)
    }

    fun convertModel(data: StopsAndRoutes): List<StopOrRoute> {
        val items: MutableList<StopOrRoute> = data.stops.map { stopName ->
            Stop(stopName)
        }.toMutableList()

        items.addAll(
            data.routes.map { route ->
                Route(route.routeID, route.isBus)
            }.sort()
        )

        return items
    }

    fun filterOutItems(
        items: List<StopOrRoute>,
        query: String
    ): List<StopOrRoute> {

        if (query.isEmpty()) {
            return items
        }

        val threshold = 70
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

    // https://stackoverflow.com/a/10831704
    private fun String.stripAccents(): String {
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

}