package com.orpington.software.rozkladmpk

import mu.KLogging
import net.grandcentrix.thirtyinch.TiPresenter

class MainPresenter(interactor: TransportLinesInteractor) : TiPresenter<MainView>() {
    private var transportLinesInteractor = interactor
    private var currentLineName: String = ""
    public val alphabet: List<String> = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "C", "D", "K", "N")

    companion object: KLogging()

    fun appendToCurrentSelection(str: String) {
        logger.info("appending $str")
        currentLineName += str
        view?.updateNameEnteredByUser(currentLineName)
        val lines = transportLinesInteractor.getLinesStartingWith(currentLineName)
        logger.info { lines }
    }




}

