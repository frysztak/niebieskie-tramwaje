package com.orpington.software.rozkladmpk.presenter

import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.adapter.RowView
import com.orpington.software.rozkladmpk.TransportLinesInteractor
import com.orpington.software.rozkladmpk.database.*
import com.orpington.software.rozkladmpk.view.NavigatingView
import kotlin.Comparator

class SpecificRoutesPresenter(
    private var interactor: TransportLinesInteractor,
    private var view: NavigatingView
) : BindingPresenter {

    private var specificRoutes: List<VariantStopDao.RouteInfo> = emptyList()

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

    override fun onBindTransportLineRowViewAtPosition(position: Int, rowView: RowView) {
        var route = specificRoutes[position]
        with(rowView) {
            setIcon(getIconIdForRoute(route.typeId))
            setName("${route.id}: ${route.firstStopName} -> ${route.lastStopName}")
            setAdditionalText(route.typeName)
        }
    }

    override fun getSize(): Int {
        return specificRoutes.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    fun onItemClicked(position: Int) {
        //view.navigateToStopActivity(stop)
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

