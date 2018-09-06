package software.orpington.rozkladmpk

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.jakewharton.espresso.OkHttp3IdlingResource
import software.orpington.rozkladmpk.CustomMatchers.Companion.withItemCount
import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.stopsAndRoutes.StopsAndRoutesActivity
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.not
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class StopsAndRoutesActivityTest {

    private fun loadJson(fileName: String): String {
        val raw = InstrumentationRegistry.getContext().assets.open("json/$fileName")
        return raw.bufferedReader().use { it.readText() }
    }

    @Rule
    @JvmField
    var activityRule = ActivityTestRule(StopsAndRoutesActivity::class.java, true, false)

    companion object {
        @ClassRule
        @JvmField
        var mockWebServer = MockWebServer()

        @BeforeClass
        @JvmStatic
        fun initApiClient() {
            val httpClient = ApiClient.getHttpClient(InstrumentationRegistry.getContext().cacheDir)

            Injection.provideDataSource(httpClient,
                mockWebServer.url("/").toString())

            val resource = OkHttp3IdlingResource.create("OkHttp", httpClient)
            Espresso.registerIdlingResources(resource)
        }
    }

    @Test
    fun loadProperly() {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(loadJson("stops_and_routes.json"))
        mockWebServer.enqueue(mockResponse)

        activityRule.launchActivity(null)

        onView(withId(R.id.recyclerView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.notFoundLayout))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.errorLayout))
            .check(matches(not(isDisplayed())))

        onView(withId(R.id.recyclerView))
            .check(matches(withItemCount(10)))
    }

    @Test
    fun return404() {
        val mockResponse = MockResponse()
            .setResponseCode(404)
        mockWebServer.enqueue(mockResponse)

        activityRule.launchActivity(null)

        onView(withId(R.id.recyclerView))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.notFoundLayout))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.errorLayout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun noResponse() {
        activityRule.launchActivity(null)

        onView(withId(R.id.recyclerView))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.notFoundLayout))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.errorLayout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun corruptedReply() {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(loadJson("stops_and_routes_corrupted.json"))
        mockWebServer.enqueue(mockResponse)

        activityRule.launchActivity(null)

        onView(withId(R.id.recyclerView))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.notFoundLayout))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.errorLayout))
            .check(matches(isDisplayed()))
    }
}