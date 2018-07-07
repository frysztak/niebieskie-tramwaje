package com.orpington.software.rozkladmpk.routeVariants

import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.TransportLinesInteractor
import com.orpington.software.rozkladmpk.adapter.HeaderListItem
import com.orpington.software.rozkladmpk.adapter.RouteListItem
import com.orpington.software.rozkladmpk.data.model.RouteVariant
import com.orpington.software.rozkladmpk.data.model.RouteVariants
import com.orpington.software.rozkladmpk.data.source.IDataSource
import com.orpington.software.rozkladmpk.database.*
import com.orpington.software.rozkladmpk.view.NavigatingView
import com.xwray.groupie.ExpandableGroup
import kotlin.Comparator
import com.orpington.software.rozkladmpk.data.source.RepositoryDataSource

class RouteVariantsPresenter(
    private val repository: RepositoryDataSource,
    private val interactor: TransportLinesInteractor,
    private val view: RouteVariantsContract.View
): RouteVariantsContract.Presenter {

    private var specificRoutes: List<RouteVariant> = emptyList()

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

    fun onItemClicked(position: Int) {
        //view.navigateToStopActivity(stop)
    }

    override fun loadVariants(stopName: String) {
        repository.getRouteVariantsForStopName(stopName, object: IDataSource.LoadDataCallback<RouteVariants> {
            override fun onDataLoaded(data: RouteVariants) {
                specificRoutes = data.routeVariants
                val sortedDistinct = specificRoutes.distinctBy { it.routeID }.sortedWith(routeInfoComparator)
                view.showVariants(sortedDistinct)
            }

            override fun onDataNotAvailable() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }, true)
        //var routes  = interactor.getRouteInfoForStop(stopName)
        //specificRoutes = sortRoutes(mergeRoutes(routes))
    }

    fun getGroupedRoutes(): List<ExpandableGroup> {
        return specificRoutes.groupBy { it.routeID }.map { pair ->
            val header = buildHeaderItem(pair.key, pair.value)
            val expandableGroup = ExpandableGroup(header)
            expandableGroup.addAll(pair.value.map { buildRouteItem(it) })
            expandableGroup
        }
    }

    private fun buildHeaderItem(
        routeId: String,
        variants: Collection<RouteVariant>
    ): HeaderListItem {
        val icon = when (variants.first().isBus) {
            true -> R.drawable.bus
            else -> R.drawable.train
        }
        val additionalText = ""//""${variants.first().typeName}, ${variants.size} wariantów"
        return HeaderListItem(routeId, icon, additionalText)
    }

    private fun buildRouteItem(
        routeInfo: RouteVariant
    ): RouteListItem {
        val name = "${routeInfo.firstStop} -> ${routeInfo.lastStop}"
        val additionalText = ""
        return RouteListItem(name, -1, additionalText)
    }

    /*
    private fun mergeRoutes(routes: List<RouteVariant>): List<RouteVariant> {
        var mergedRoutes: List<VariantStopDao.RouteInfo> = emptyList()
        var groups = routes.groupBy { it.routeID }
        for ((key, items) in groups) {
            val routeInfo = items.first()
            val combinedTripIDs= items.map { it.tripIDs.first() }
            mergedRoutes += RouteVariant(key, )

        }
        return mergedRoutes
    }*/

    private val routeInfoComparator = Comparator<RouteVariant> { p0, p1 ->
        var intId0 = p0.routeID.toIntOrNull()
        var intId1 = p1.routeID.toIntOrNull()
        if (intId0 != null && intId1 != null) {
            intId0.compareTo(intId1)
        } else {
            p0.routeID.compareTo(p1.routeID)
        }
    }

    private fun sortRoutes(routes: List<RouteVariant>): List<RouteVariant> {
        return routes.sortedWith(routeInfoComparator)
        var sortedRoutes: List<RouteVariant> = emptyList()

        /*
        var groups = routes.groupBy { it.typeId }.mapValues { it.value.toMutableList() }
        groups.forEach { it.value.sortWith(routeInfoComparator)}
        val order = arrayOf(35, 30, 31, 34, 39, 40)

        order.forEach {
            if (it in groups) {
                sortedRoutes += groups[it]!!
            }
        }
        */

        return sortedRoutes
    }
}

