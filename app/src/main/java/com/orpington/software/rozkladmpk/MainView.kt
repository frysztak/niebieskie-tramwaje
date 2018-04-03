package com.orpington.software.rozkladmpk

import net.grandcentrix.thirtyinch.TiView
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread

interface MainView: TiView {
    @CallOnMainThread
    fun updateNameEnteredByUser(name: String)
}