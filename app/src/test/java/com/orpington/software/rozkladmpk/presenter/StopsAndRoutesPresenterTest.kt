package com.orpington.software.rozkladmpk.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.orpington.software.rozkladmpk.TransportLinesInteractor
import com.orpington.software.rozkladmpk.view.NavigatingView
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.Assert.*

class StopsAndRoutesPresenterTest: Spek({
    given("test") {
        val mockView = mock<NavigatingView> {}
        val mockInteractor = mock<TransportLinesInteractor> {
            on { getStopNamesStartingWith(any()) } doReturn listOf("KOZANÓW", "Na Ostatnim Groszu")
        }
        val presenter = StopsAndRoutesPresenter(mockInteractor, mockView)

        on("query text changed") {
            presenter.onQueryTextChange("")

            it("Should return 2") {
                // check if lists are populated
            }
        }
    }
})