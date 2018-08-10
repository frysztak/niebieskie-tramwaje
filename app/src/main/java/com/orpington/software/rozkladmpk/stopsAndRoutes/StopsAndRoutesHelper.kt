package com.orpington.software.rozkladmpk.stopsAndRoutes

import com.orpington.software.rozkladmpk.data.model.StopsAndRoutes

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
        return items.filter { item ->
            when (item) {
                is Stop -> item.stopName.startsWith(query, true)
                is Route -> item.routeID.startsWith(query, true)
            }

        }
    }

}