package com.orpington.software.rozkladmpk

import mu.KLogging
import net.grandcentrix.thirtyinch.TiPresenter

class MainPresenter(interactor: TransportLinesInteractor) : TiPresenter<MainView>() {
    private var transportLinesInteractor = interactor
    private var currentLineName: String = ""

    companion object: KLogging()

    fun userEnteredCharacter(str: String) {
        logger.info("user entered $str")

        when (str) {
            "↩" ->  currentLineName = currentLineName.substring(0..currentLineName.length-2) // FIXME: additional checks
            else -> currentLineName += str
        }

        view?.updateNameEnteredByUser(currentLineName)
        val lines = transportLinesInteractor.getLinesStartingWith(currentLineName)
        logger.info { lines }
    }




}

