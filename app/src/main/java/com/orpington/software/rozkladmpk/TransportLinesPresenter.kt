package com.orpington.software.rozkladmpk

import com.orpington.software.rozkladmpk.database.Station
import com.orpington.software.rozkladmpk.database.TransportLine
import com.orpington.software.rozkladmpk.database.TransportType

class TransportLinesPresenter(private var interactor: TransportLinesInteractor) {
    private var transportLines: List<TransportLine> = emptyList()
    private var stations: List<Station> = emptyList()

    @Suppress("PrivatePropertyName")
    private var VIEW_TYPE_STATION:        Int = 0
    @Suppress("PrivatePropertyName")
    private var VIEW_TYPE_TRANSPORT_LINE: Int = 1

    fun onBindTransportLineRowViewAtPosition(position: Int, rowView: RowView) {
        var viewType = getItemViewType(position)
        var idx = getIdxForType(viewType, position)

        when (viewType) {
            VIEW_TYPE_TRANSPORT_LINE -> {
                var line = transportLines[idx]
                with(rowView) {
                    setIcon(when (line.type) {
                        TransportType.BUS -> R.drawable.bus
                        TransportType.TRAM -> R.drawable.train
                    })
                    setName(interactor.getFullLineName(line.id))
                    setAdditionalText("")
                }
            }
            VIEW_TYPE_STATION -> {
                var station = stations[idx]
                with(rowView) {
                    setIcon(R.drawable.traffic_light)
                    setName(station.name)
                    setAdditionalText(station.info)
                }
            }
        }
    }

    fun getSize(): Int {
        return transportLines.size + stations.size
    }

    fun getItemViewType(position: Int): Int {
        return if (position < stations.size) {
            VIEW_TYPE_STATION
        } else {
            VIEW_TYPE_TRANSPORT_LINE
        }
    }

    private fun getIdxForType(viewType: Int, idx: Int): Int {
        return when (viewType) {
            VIEW_TYPE_STATION -> idx
            VIEW_TYPE_TRANSPORT_LINE -> idx - stations.size
            else -> -1
        }
    }

    fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            stations = interactor.getStationsStartingWith(newText)
            transportLines = interactor.getLinesStartingWith(newText)
            return true
        }
        return false
    }
}

