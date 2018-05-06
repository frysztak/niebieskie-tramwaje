package com.orpington.software.rozkladmpk

import com.orpington.software.rozkladmpk.database.Station
import com.orpington.software.rozkladmpk.database.TransportLine
import com.orpington.software.rozkladmpk.database.TransportType

class TransportLinesPresenter(private var interactor: TransportLinesInteractor) {
    private var transportLines: List<TransportLine> = emptyList()
    private var stations: List<Station> = emptyList()

    @Suppress("PrivatePropertyName")
    private var VIEW_TYPE_TRANSPORT_LINE: Int = 0
    @Suppress("PrivatePropertyName")
    private var VIEW_TYPE_STATION:        Int = 1

    fun onBindTransportLineRowViewAtPosition(position: Int, rowView: RowView) {
        var viewType = getItemViewType(position)
        var idx = getIdxForType(viewType, position)

        when (viewType) {
            VIEW_TYPE_TRANSPORT_LINE -> {
                var line = transportLines[idx]
                rowView.setIcon(if (line.type == TransportType.BUS) R.drawable.bus else R.drawable.train)
                rowView.setName(transportLines[idx].prettyName)
                rowView.setAdditionalText("")
            }
            VIEW_TYPE_STATION -> {
                var station = stations[idx]
                rowView.setIcon(R.drawable.traffic_light)
                rowView.setName(station.name)
                rowView.setAdditionalText(station.info)
            }
        }
    }

    fun getSize(): Int {
        return transportLines.size + stations.size
    }

    fun getItemViewType(position: Int): Int {
        return if (position < transportLines.size) {
            VIEW_TYPE_TRANSPORT_LINE
        } else {
            VIEW_TYPE_STATION
        }
    }

    private fun getIdxForType(viewType: Int, idx: Int): Int {
        return when (viewType) {
            VIEW_TYPE_TRANSPORT_LINE -> idx
            VIEW_TYPE_STATION -> idx - transportLines.size
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

