package com.orpington.software.rozkladmpk.presenter

import com.orpington.software.rozkladmpk.R
import com.orpington.software.rozkladmpk.adapter.RowView
import com.orpington.software.rozkladmpk.TransportLinesInteractor
import com.orpington.software.rozkladmpk.database.*
import com.orpington.software.rozkladmpk.view.NavigatingView

class StopsAndRoutesPresenter(
    private var interactor: TransportLinesInteractor,
    private var view: NavigatingView
): BindingPresenter {
    private var stops: List<String> = emptyList()
    private var generalRoutes: List<Route> = emptyList()

    private val VIEW_TYPE_STOP:        Int = 0
    private val VIEW_TYPE_GENERAL_ROUTE: Int = 1

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
        }
    }

    override fun getSize(): Int {
        return generalRoutes.size + stops.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position < stops.size -> VIEW_TYPE_STOP
            else -> VIEW_TYPE_GENERAL_ROUTE
        }
    }

    private fun getIdxForType(viewType: Int, idx: Int): Int {
        return when (viewType) {
            VIEW_TYPE_STOP -> idx
            VIEW_TYPE_GENERAL_ROUTE -> idx - stops.size
            else -> -1
        }
    }

    fun onQueryTextChange(newText: String?) {
        if (newText != null) {
            stops = interactor.getStopNamesStartingWith(newText)
            generalRoutes = interactor.getLinesStartingWith(newText)
        }
    }

    fun onItemClicked(position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_STOP -> {
                var stop = stops[getIdxForType(VIEW_TYPE_STOP, position)]
                view.navigateToStopActivity(stop)
            }
        }
    }

}

