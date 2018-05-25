package com.orpington.software.rozkladmpk.presenter

import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.TransportLinesInteractor
import com.orpington.software.rozkladmpk.adapter.RouteListItem
import com.orpington.software.rozkladmpk.database.Route
import com.orpington.software.rozkladmpk.database.RouteTypeEnum
import com.orpington.software.rozkladmpk.database.getEnumForRouteType
import com.orpington.software.rozkladmpk.view.NavigatingView
import com.xwray.groupie.Section

class StopsAndRoutesPresenter(
    private var interactor: TransportLinesInteractor,
    private var view: NavigatingView
) {
    private var stops: List<String> = emptyList()
    private var generalRoutes: List<Route> = emptyList()

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

    fun onQueryTextChange(newText: String?) {
        if (newText != null) {
            stops = interactor.getStopNamesStartingWith(newText)
            generalRoutes = interactor.getLinesStartingWith(newText)
        }
    }

    fun onItemClicked(id: String) {
        view.navigateToStopActivity(id)
    }

    private fun buildStopItem(
        name: String
    ): RouteListItem {
        val name = name
        val icon = R.drawable.traffic_light
        val additionalText = ""
        return RouteListItem(name, icon, additionalText)
    }

    private fun buildRouteItem(
        name: String,
        iconId: Int,
        additionalText: String
    ): RouteListItem {
        return RouteListItem(name, iconId, additionalText)
    }

    fun getListSection(): Section {
        val section = Section()
        section.addAll(stops.map { buildStopItem(it) })
        section.addAll(generalRoutes.map { route ->
            val routeType = interactor.getRouteType(route.id)
            buildRouteItem(route.id, getIconIdForRoute(routeType.id), routeType.name)
        })

        return section
    }

}

