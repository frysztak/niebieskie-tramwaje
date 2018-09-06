package software.orpington.rozkladmpk

import software.orpington.rozkladmpk.data.model.StopsAndRoutes
import software.orpington.rozkladmpk.stopsAndRoutes.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.hamcrest.CoreMatchers.`is` as Is

class StopsAndRoutesHelperTest {

    private lateinit var helper: StopsAndRoutesHelper

    @Before
    fun setup() {
        helper = StopsAndRoutesHelper()
    }

    @Test
    fun stripAccents_Test() {
        val lowercase = "ąćęłńóśżź"
        val lowercaseExpected = "acelnoszz"
        assertThat(lowercase.stripAccents(), Is(lowercaseExpected))

        val uppercase = "ĄĆĘŁŃÓŚŻŹ"
        val uppercaseExpected = "ACELNOSZZ"
        assertThat(uppercase.stripAccents(), Is(uppercaseExpected))

        val kozanow = "KOZANÓW"
        val kozanowExpected = "KOZANOW"
        assertThat(kozanow.stripAccents(), Is(kozanowExpected))
    }

    @Test
    fun convertModel_Test() {
        val data = StopsAndRoutes(
            listOf(
                StopsAndRoutes.Stop("AUCHAN", 0.0, 0.0),
                StopsAndRoutes.Stop("KOZANÓW", 0.0, 0.0)),
            listOf(
                StopsAndRoutes.Route("122", true),
                StopsAndRoutes.Route("33", false)
            )
        )

        val actual = helper.convertModel(data)
        val expected: List<StopOrRoute> = listOf(
            Stop("AUCHAN"),
            Stop("KOZANÓW"),
            Route("33", false),
            Route("122", true)
        )

        assertThat(actual, Is(expected))
    }

    @Test
    fun filterItems_emptyQuery() {
        val items: List<StopOrRoute> = listOf(
            Stop("AUCHAN"),
            Stop("KOZANÓW"),
            Route("33", false),
            Route("122", true)
        )

        val actual = helper.filterItems(items, "")
        assertThat(actual, Is(items))
    }

    @Test
    fun filterItems_queryCorrespondingToExpressBus() {
        val items: List<StopOrRoute> = listOf(
            Stop("C.H. Korona"),
            Stop("KOZANÓW"),
            Route("33", false),
            Route("C", true)
        )

        val actual = helper.filterItems(items, "C")
        val expected: List<StopOrRoute> = listOf(
            Route("C", true)
        )
        assertThat(actual, Is(expected))
    }

    @Test
    fun filterItems_queryCorrespondingToRoutes() {
        val items: List<StopOrRoute> = listOf(
            Stop("AUCHAN"),
            Stop("KOZANÓW"),
            Route("3", false),
            Route("32", false),
            Route("33", false),
            Route("C", true)
        )

        val actual = helper.filterItems(items, "33")
        val expected: List<StopOrRoute> = listOf(
            Route("33", false),
            Route("3", false)
        )
        assertThat(actual, Is(expected))
    }

    @Test
    fun filterItems_queryCorrespondingToStop() {
        val items: List<StopOrRoute> = listOf(
            Stop("Na Ostatnim Groszu"),
            Stop("KOZANÓW"),
            Route("3", false),
            Route("32", false),
            Route("33", false),
            Route("C", true)
        )

        val actual = helper.filterItems(items, "grosz")
        val expected: List<StopOrRoute> = listOf(
            Stop("Na Ostatnim Groszu")
        )
        assertThat(actual, Is(expected))
    }
}