package com.orpington.software.rozkladmpk

import com.orpington.software.rozkladmpk.database.TransportLine
import net.grandcentrix.thirtyinch.TiView
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread

interface MainView: TiView {
    @CallOnMainThread
    fun updateCurrentLines(lines: List<TransportLine>, enteredByUser: String)
}