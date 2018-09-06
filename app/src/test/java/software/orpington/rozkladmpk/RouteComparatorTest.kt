package software.orpington.rozkladmpk

import software.orpington.rozkladmpk.data.model.RouteVariant
import software.orpington.rozkladmpk.stopsAndRoutes.Route
import software.orpington.rozkladmpk.utils.sort
import org.junit.Assert.assertThat
import org.junit.Test
import org.hamcrest.CoreMatchers.`is` as Is

class RouteComparatorTest {
    @Test
    fun sortRoutes() {
        val data = listOf(
            Route("3", false),
            Route("1", false),
            Route("0L", false),
            Route("122", true)
        )

        val actual = data.sort()
        val expected = listOf(
            Route("0L", false),
            Route("1", false),
            Route("3", false),
            Route("122", true)
        )

        assertThat(actual, Is(expected))
    }

    @Test
    fun sortRoutesWithExpressRoute() {
        val data = listOf(
            Route("3", false),
            Route("1", false),
            Route("0L", false),
            Route("C", true)
        )

        val actual = data.sort()
        val expected = listOf(
            Route("C", true),
            Route("0L", false),
            Route("1", false),
            Route("3", false)
        )

        assertThat(actual, Is(expected))
    }

    @Test
    fun sortJustExpressRoutes() {
        val data = listOf(
            Route("D", true),
            Route("C", true),
            Route("A", true)
        )

        val actual = data.sort()
        val expected = listOf(
            Route("A", true),
            Route("C", true),
            Route("D", true)
        )

        assertThat(actual, Is(expected))
    }

    @Test
    fun sortRoutesWithMultipleExpressRoutes() {
        val data = listOf(
            Route("A", true),
            Route("3", false),
            Route("D", true),
            Route("1", false),
            Route("0L", false),
            Route("C", true)
        )

        val actual = data.sort()
        val expected = listOf(
            Route("A", true),
            Route("C", true),
            Route("D", true),
            Route("0L", false),
            Route("1", false),
            Route("3", false)
        )

        assertThat(actual, Is(expected))
    }

    @Test
    fun sortRouteVariants() {
        val data = listOf(
            RouteVariant("A", true, "", "", emptyList()),
            RouteVariant("33", false, "", "", emptyList()),
            RouteVariant("0L", false, "", "", emptyList())
        )

        val actual = data.sort()
        val expected = listOf(
            RouteVariant("A", true, "", "", emptyList()),
            RouteVariant("0L", false, "", "", emptyList()),
            RouteVariant("33", false, "", "", emptyList())
        )

        assertThat(actual, Is(expected))
    }
}