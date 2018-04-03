package com.orpington.software.rozkladmpk

import com.orpington.software.rozkladmpk.database.TransportLine

interface TransportLinesInteractor {
    fun getLinesStartingWith(str: String): List<TransportLine>
}