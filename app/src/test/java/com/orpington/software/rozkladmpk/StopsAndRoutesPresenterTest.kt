package com.orpington.software.rozkladmpk

import com.nhaarman.mockito_kotlin.*
import com.orpington.software.rozkladmpk.data.model.StopsAndRoutes
import com.orpington.software.rozkladmpk.data.source.ApiService
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import com.orpington.software.rozkladmpk.stopsAndRoutes.Stop
import com.orpington.software.rozkladmpk.stopsAndRoutes.StopOrRoute
import com.orpington.software.rozkladmpk.stopsAndRoutes.StopsAndRoutesContract
import com.orpington.software.rozkladmpk.stopsAndRoutes.StopsAndRoutesPresenter
import com.orpington.software.rozkladmpk.utils.CurrentThreadExecutor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File


class StopsAndRoutesPresenterTest {
    private lateinit var dataSource: RemoteDataSource
    private lateinit var mockServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var presenter: StopsAndRoutesPresenter
    private lateinit var view: StopsAndRoutesContract.View


    private fun loadJson(fileName: String): String {
        val url = this.javaClass.classLoader.getResource("json/$fileName")
        val file = File(url.path)
        return String(file.readBytes())
    }

    @Throws
    @Before
    fun setUp() {
        // Initialize mock webserver
        mockServer = MockWebServer()
        // Start the local server
        mockServer.start()

        // Get an okhttp client
        val currentThreadExecutor = CurrentThreadExecutor()
        val dispatcher = okhttp3.Dispatcher(currentThreadExecutor)
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(interceptor)
            .dispatcher(dispatcher)
            .build()

        // Get an instance of Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(mockServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .callbackExecutor(currentThreadExecutor)
            .build()

        // Get an instance of blogService
        apiService = retrofit.create(ApiService::class.java)
        // Initialized repository
        dataSource = RemoteDataSource.getInstance(apiService)

        view = mock {}
        presenter = StopsAndRoutesPresenter(dataSource)
        presenter.attachView(view)
    }

    @After
    @Throws
    fun tearDown() {
        // We're done with tests, shut it down
        mockServer.shutdown()
    }

    @Test
    fun loadStopsAndRoutes() {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(loadJson("stops_and_routes.json"))
        mockServer.enqueue(mockResponse)

        presenter.loadStopsAndRoutes()

        val inOrder = inOrder(view)
        inOrder.verify(view, times(1)).showProgressBar()
        inOrder.verify(view, times(1)).hideProgressBar()
        inOrder.verify(view, times(1)).setStopsAndRoutes(any())
        verify(view, never()).reportThatSomethingWentWrong()
    }

    @Test
    fun loadStopNamesTest_404() {
        val mockResponse = MockResponse()
            .setResponseCode(404)
        mockServer.enqueue(mockResponse)

        val inOrder = inOrder(view)
        presenter.loadStopsAndRoutes()
        inOrder.verify(view, times(1)).showProgressBar()
        inOrder.verify(view, times(1)).hideProgressBar()
        inOrder.verify(view, times(1)).reportThatSomethingWentWrong()
        verify(view, never()).setSearchResults(any())
    }

    @Test
    fun queryTextChangedTest() {
        val data = StopsAndRoutes(
            listOf(StopsAndRoutes.Stop("BISKUPIN", 0.0, 0.0),
                StopsAndRoutes.Stop("AUCHAN", 0.0, 0.0),
                StopsAndRoutes.Stop("Bierzyce", 0.0, 0.0),
                StopsAndRoutes.Stop("Adamieckiego", 0.0, 0.0)),
            listOf(StopsAndRoutes.Route("33", false))
        )

        val expected = listOf<StopOrRoute>(
            Stop("AUCHAN"),
            Stop("Adamieckiego")
        )

        presenter.setStopsAndRoutes(data)
        presenter.queryTextChanged("a")
        verify(view).setSearchResults(expected)
        verify(view).showStopsList()
        verify(view, never()).showProgressBar()
        verify(view, never()).hideProgressBar()
        verify(view, never()).reportThatSomethingWentWrong()
    }

    @Test
    fun queryTextChangedTest_EmptyList() {
        val data = StopsAndRoutes(
            emptyList(), emptyList()
        )

        presenter.setStopsAndRoutes(data)
        presenter.queryTextChanged("a")
        verify(view, never()).setSearchResults(any())
        verify(view, never()).showProgressBar()
        verify(view, never()).hideProgressBar()
        verify(view, never()).reportThatSomethingWentWrong()
        verify(view, never()).showStopsList()
        verify(view, times(1)).showStopNotFound()
    }

    @Test
    fun backpressWhenNoResultsScreenIsShown() {
        val data = StopsAndRoutes(
            emptyList(), emptyList()
        )

        presenter.setStopsAndRoutes(data)
        presenter.queryTextChanged("a")
        val inOrder = inOrder(view)
        inOrder.verify(view, times(1)).showStopNotFound()
        presenter.queryTextChanged("") // empty string is sent when user presses "<-" button
        inOrder.verify(view, times(1)).showStopsList()
    }

    @Test
    fun locationChangedWhenNoResultsScreenIsVisible() {
        val data = StopsAndRoutes(
            emptyList(), emptyList()
        )

        presenter.setStopsAndRoutes(data)
        presenter.queryTextChanged("a")
        verify(view, times(1)).showStopNotFound()

        presenter.locationChanged(0.0, 0.0)
        verify(view, never()).showStopsList()
    }
}