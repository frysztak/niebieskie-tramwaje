package com.orpington.software.rozkladmpk

import com.orpington.software.rozkladmpk.database.*
import kotlin.Comparator

class TransportLinesPresenter(
    private var interactor: TransportLinesInteractor,
    private var view: NavigatingView
) {
    private var stops: List<String> = emptyList()
    private var generalRoutes: List<Route> = emptyList()
    private var specificRoutes: List<VariantStopDao.RouteInfo> = emptyList()

    @Suppress("PrivatePropertyName")
    private var VIEW_TYPE_STOP:        Int = 0
    @Suppress("PrivatePropertyName")
    private var VIEW_TYPE_GENERAL_ROUTE: Int = 1
    @Suppress("PrivatePropertyName")
    private var VIEW_TYPE_SPECIFIC_ROUTE: Int = 2

    private fun getIconIdForRoute(routeTypeId: Int): Int {
        return when (getEnumForRouteType(routeTypeId)) {
            RouteTypeEnum.NORMAL_BUS,
            RouteTypeEnum.SUBURBAN_BUS,
            RouteTypeEnum.EXPRESS_BUS,
            RouteTypeEnum.ZONE_BUS,
            RouteTypeEnum.NIGHT_BUS -> R.drawable.bus

            RouteTypeEnum.NORMAL_TRAM -> R.drawable.train
            RouteTypeEnum.INVALID -> R.drawable.train // FIXME
        }
    }

    fun onBindTransportLineRowViewAtPosition(position: Int, rowView: RowView) {
        var viewType = getItemViewType(position)
        var idx = getIdxForType(viewType, position)

        when (viewType) {
            VIEW_TYPE_GENERAL_ROUTE -> {
                var route = generalRoutes[idx]
                var routeType = interactor.getRouteType(route.id)
                with(rowView) {
                    setIcon(getIconIdForRoute(routeType.id))
                    setName(route.id)
                    setAdditionalText(routeType.name)
                }
            }

            VIEW_TYPE_STOP -> {
                var stop = stops[idx]
                with(rowView) {
                    setIcon(R.drawable.traffic_light)
                    setName(stop)
                    setAdditionalText("")
                }
            }

            VIEW_TYPE_SPECIFIC_ROUTE -> {
                var route = specificRoutes[idx]
                with(rowView) {
                    setIcon(getIconIdForRoute(route.typeId))
                    setName("${route.id}: ${route.firstStopName} -> ${route.lastStopName}")
                    setAdditionalText(route.typeName)
                }
            }
        }
    }

    fun getSize(): Int {
        return generalRoutes.size + stops.size + specificRoutes.size
    }

    fun getItemViewType(position: Int): Int {
        return when {
            position < stops.size -> VIEW_TYPE_STOP
            position < stops.size + generalRoutes.size -> VIEW_TYPE_GENERAL_ROUTE
            else -> VIEW_TYPE_SPECIFIC_ROUTE
        }
    }

    private fun getIdxForType(viewType: Int, idx: Int): Int {
        return when (viewType) {
            VIEW_TYPE_STOP -> idx
            VIEW_TYPE_GENERAL_ROUTE -> idx - stops.size
            VIEW_TYPE_SPECIFIC_ROUTE -> idx - stops.size - generalRoutes.size
            else -> -1
        }
    }

    fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            stops = interactor.getStopNamesStartingWith(newText)
            generalRoutes = interactor.getLinesStartingWith(newText)
            return true
        }
        return false
    }

    fun onItemClicked(position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_STOP -> {
                var stop = stops[getIdxForType(VIEW_TYPE_STOP, position)]
                view.navigateToStopActivity(stop)
            }
        }
    }

    fun loadRoutesForStop(stopName: String) {
        var routes  = interactor.getRouteInfoForStop(stopName)
        specificRoutes = sortRoutes(mergeRoutes(routes))
    }

    private fun mergeRoutes(routes: List<VariantStopDao.RouteInfo>): List<VariantStopDao.RouteInfo> {
        var mergedRoutes: List<VariantStopDao.RouteInfo> = emptyList()
        var groups = routes.groupBy { it.id + it.firstStopName + it.lastStopName }
        for ((key, items) in groups) {
            var routeInfo = items.first()
            routeInfo.variantIds = items.map { it.variantIds.first() }
            mergedRoutes += routeInfo

        }
        return mergedRoutes
    }

    private val routeInfoComparator = Comparator<VariantStopDao.RouteInfo> { p0, p1 ->
        var intId0 = p0.id.toIntOrNull()
        var intId1 = p1.id.toIntOrNull()
        if (intId0 != null && intId1 != null) {
            intId0.compareTo(intId1)
        } else {
            p0.id.compareTo(p1.id)
        }
    }

    private fun sortRoutes(routes: List<VariantStopDao.RouteInfo>): List<VariantStopDao.RouteInfo> {
        var sortedRoutes: List<VariantStopDao.RouteInfo> = emptyList()

        var groups = routes.groupBy { it.typeId }.mapValues { it.value.toMutableList() }
        groups.forEach { it.value.sortWith(routeInfoComparator)}
        val order = arrayOf(35, 30, 31, 34, 39, 40)

        order.forEach {
            if (it in groups) {
                sortedRoutes += groups[it]!!
            }
        }

        return sortedRoutes
    }
}

