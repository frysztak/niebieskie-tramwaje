package software.orpington.rozkladmpk.utils

import software.orpington.rozkladmpk.data.model.RouteVariant
import software.orpington.rozkladmpk.home.Route

internal class RouteComparator {
    fun compare(routeID1: String, routeID2: String): Int {
        val id1 = routeID1.toIntOrNull()
        val id2 = routeID2.toIntOrNull()

        if (id1 != null && id2 != null) {
            // two ints (e.g. 32 and 33)
            return id1.compareTo(id2)
        }

        // handle express lines: A, C, D, K, N
        val letter1 = if (routeID1.length == 1 && routeID1[0].isLetter()) routeID1.first() else null
        val letter2 = if (routeID2.length == 1 && routeID2[0].isLetter()) routeID2.first() else null

        if (letter1 != null && letter2 != null) {
            // both route are express
            return letter1.compareTo(letter2)
        } else if (letter1 != null) {
            // first route is express. we want it to be at the top of the list,
            // so return `-1` which signifies that `routeID1 < routeID2`
            return -1
        } else if (letter2 != null) {
            // second route is express. we want it to be at the top of the list,
            // so return `1` which signifies that `routeID1 > routeID2`
            return 1
        }

        // most generic case, compare two strings
        return routeID1.compareTo(routeID2)
    }
}

@JvmName("sortRoutes")
fun <T : List<Route>> T.sort(): List<Route> {
    val comp = Comparator<Route> { r1, r2 ->
        RouteComparator().compare(r1.routeID, r2.routeID)
    }
    return this.sortedWith(comp)
}

@JvmName("sortRouteVariants")
fun <T : List<RouteVariant>> T.sort(): List<RouteVariant> {
    val comp = Comparator<RouteVariant> { r1, r2 ->
        RouteComparator().compare(r1.routeID, r2.routeID)
    }
    return this.sortedWith(comp)
}