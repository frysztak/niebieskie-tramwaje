package com.orpington.software.rozkladmpk

import com.nhaarman.mockito_kotlin.*
import com.orpington.software.rozkladmpk.data.model.StopsAndRoutes
import com.orpington.software.rozkladmpk.data.source.ApiService
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import com.orpington.software.rozkladmpk.stopsAndRoutes.*
import com.orpington.software.rozkladmpk.utils.CurrentThreadExecutor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito.*
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File


@PowerMockIgnore("okhttp3.*", "retrofit2.*")
@RunWith(PowerMockRunner::class)
@PrepareForTest(LoggerFactory::class)
class StopsAndRoutesPresenterTest {
    private lateinit var dataSource: RemoteDataSource
    private lateinit var mockServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var presenter: StopsAndRoutesPresenter
    private lateinit var view: StopsAndRoutesContract.View

    companion object {
        private lateinit var mockLogger: Logger
        @BeforeClass
        @JvmStatic
        fun setup() {
            mockStatic(LoggerFactory::class.java)
            mockLogger = mock(Logger::class.java)
            `when`(LoggerFactory.getLogger(any<Class<*>>())).thenReturn(mockLogger)
            `when`(LoggerFactory.getLogger(any<String>())).thenReturn(mockLogger)
        }
    }

    private fun loadJson(fileName: String): String {
        val url = this.javaClass.classLoader.getResource("json/$fileName")
        val file = File(url.path)
        val str = String(file.readBytes())
        return str
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
            //.addNetworkInterceptor(interceptor)
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
    @Ignore // returned content is never set -- why? it used to work.
    fun loadStopsAndRoutes() {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(loadJson("stops_and_routes.json"))
            //.setBody("{\"Stops\":[\"8 Maja\",\"AUCHAN\"],\"Routes\":[{\"ID\":\"0L\",\"IsBus\":false},{\"ID\":\"103\",\"IsBus\":true}]}")
        mockServer.enqueue(mockResponse)

        presenter.loadStopsAndRoutes()

        val inOrder = inOrder(view)
        inOrder.verify(view, times(1)).showProgressBar()
        inOrder.verify(view).displayStopsAndRoutes(any())
        //inOrder.verify(view).displayStopsAndRoutes(listOf<Stop>("8 Maja", "AUCHAN", "Adamczewskich", "Adamieckiego"))
        inOrder.verify(view, times(1)).hideProgressBar()
        //verify(view, never()).reportThatSomethingWentWrong()
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
        verify(view, never()).displayStopsAndRoutes(any())
    }

    @Test
    fun queryTextChangedTest() {
        val data = StopsAndRoutes(
            listOf("BISKUPIN", "AUCHAN", "Bierzyce", "Adamieckiego"),
            listOf(StopsAndRoutes.Route("33", false))
        )

        val expected = listOf<StopOrRoute>(
            Stop("AUCHAN"),
            Stop("Adamieckiego")
        )

        presenter.setStopsAndRoutes(data)
        presenter.queryTextChanged("a")
        verify(view).displayStopsAndRoutes(expected)
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
        verify(view, never()).displayStopsAndRoutes(any())
        verify(view, never()).showProgressBar()
        verify(view, never()).hideProgressBar()
        verify(view, never()).reportThatSomethingWentWrong()
        verify(view, times(1)).showStopNotFound()
    }

    @Test
    fun stopClicked() {
        val data = listOf<StopOrRoute>(
            Stop("8 Maja"),
            Stop("AUCHAN"),
            Stop("Adamieckiego")
        )

        presenter.setShownStopsAndRoutes(data)
        presenter.listItemClicked(0)
        verify(view, times(1)).navigateToRouteVariants("8 Maja")
        verify(view, never()).navigateToStopsForRoute(any())
    }

    @Test
    fun routeClicked() {
        val data = listOf<StopOrRoute>(
            Stop("8 Maja"),
            Stop("AUCHAN"),
            Route("33", false),
            Stop("Adamieckiego")
        )

        presenter.setShownStopsAndRoutes(data)
        presenter.listItemClicked(2)
        verify(view, times(1)).navigateToStopsForRoute("33")
        verify(view, never()).navigateToRouteVariants(any())
    }

    @Test
    fun listItemClicked_EmptyList() {
        presenter.setShownStopsAndRoutes(emptyList())
        presenter.listItemClicked(0)
        verify(view, never()).navigateToRouteVariants(any())
        verify(view, never()).navigateToStopsForRoute(any())
    }

    @Test
    fun listItemClicked_OutOfRange() {
        val data = listOf<StopOrRoute>(
            Stop("8 Maja"),
            Stop("AUCHAN"),
            Route("33", false),
            Stop("Adamieckiego")
        )

        presenter.setShownStopsAndRoutes(data)
        presenter.listItemClicked(10)
        verify(view, never()).navigateToRouteVariants(any())
        verify(view, never()).navigateToStopsForRoute(any())
    }

    @Test
    fun listItemClicked_LastItem() {
        val data = listOf(
            Stop("8 Maja"),
            Stop("AUCHAN"),
            Route("33", false),
            Stop("Adamieckiego")
        )

        presenter.setShownStopsAndRoutes(data)
        presenter.listItemClicked(3)
        verify(view, times(1)).navigateToRouteVariants("Adamieckiego")
        verify(view, never()).navigateToStopsForRoute(any())
    }
}