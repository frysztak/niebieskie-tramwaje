package com.orpington.software.rozkladmpk

import com.orpington.software.rozkladmpk.database.Stop
import com.orpington.software.rozkladmpk.database.Route
import com.orpington.software.rozkladmpk.database.RouteTypeEnum
import com.orpington.software.rozkladmpk.database.getEnumForRouteType

//import com.orpington.software.rozkladmpk.database.TransportType

class TransportLinesPresenter(
    private var interactor: TransportLinesInteractor,
    private var view: NavigatingView
) {
    private var route: List<Route> = emptyList()
    private var stops: List<String> = emptyList()

    @Suppress("PrivatePropertyName")
    private var VIEW_TYPE_STATION:        Int = 0
    @Suppress("PrivatePropertyName")
    private var VIEW_TYPE_TRANSPORT_LINE: Int = 1

    fun onBindTransportLineRowViewAtPosition(position: Int, rowView: RowView) {
        var viewType = getItemViewType(position)
        var idx = getIdxForType(viewType, position)

        when (viewType) {
            VIEW_TYPE_TRANSPORT_LINE -> {
                var route = route[idx]
                var routeType = interactor.getRouteType(route.id)
                with(rowView) {
                    setIcon(when (getEnumForRouteType(routeType)) {
                        RouteTypeEnum.NORMAL_BUS,
                        RouteTypeEnum.SUBURBAN_BUS,
                        RouteTypeEnum.EXPRESS_BUS,
                        RouteTypeEnum.ZONE_BUS,
                        RouteTypeEnum.NIGHT_BUS -> R.drawable.bus

                        RouteTypeEnum.NORMAL_TRAM -> R.drawable.train
                        RouteTypeEnum.INVALID -> R.drawable.train // FIXME
                    })
                    setName(route.id)
                    setAdditionalText("")
                }
            }
            VIEW_TYPE_STATION -> {
                var stop = stops[idx]
                with(rowView) {
                    setIcon(R.drawable.traffic_light)
                    setName(stop)
                    //setAdditionalText(station.info)
                }
            }
        }
    }

    fun getSize(): Int {
        return route.size + stops.size
    }

    fun getItemViewType(position: Int): Int {
        return if (position < stops.size) {
            VIEW_TYPE_STATION
        } else {
            VIEW_TYPE_TRANSPORT_LINE
        }
    }

    private fun getIdxForType(viewType: Int, idx: Int): Int {
        return when (viewType) {
            VIEW_TYPE_STATION -> idx
            VIEW_TYPE_TRANSPORT_LINE -> idx - stops.size
            else -> -1
        }
    }

    fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            stops = interactor.getStopNamesStartingWith(newText)
            route = interactor.getLinesStartingWith(newText)
            return true
        }
        return false
    }

    fun onItemClicked(position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_STATION -> {
                var station = stops[getIdxForType(VIEW_TYPE_STATION, position)]
                view.navigateToStationActivity(0) // FIXME
            }
        }
    }

    fun loadLinesForStation(stationId: Int) {
        route = interactor.getLinesForStation(stationId)
    }
}

