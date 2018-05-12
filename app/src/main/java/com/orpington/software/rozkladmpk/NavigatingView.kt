package com.orpington.software.rozkladmpk

import net.grandcentrix.thirtyinch.TiView
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread

interface NavigatingView: TiView {
    @CallOnMainThread
    fun navigateToStationActivity(stationId: Int)
}