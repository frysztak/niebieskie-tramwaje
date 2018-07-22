package com.orpington.software.rozkladmpk

import com.nhaarman.mockito_kotlin.*
import com.orpington.software.rozkladmpk.data.source.ApiService
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import com.orpington.software.rozkladmpk.stopsAndRoutes.StopsAndRoutesContract
import com.orpington.software.rozkladmpk.stopsAndRoutes.StopsAndRoutesPresenter
import com.orpington.software.rozkladmpk.utils.CurrentThreadExecutor
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito.*
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
        val okHttpClient = OkHttpClient.Builder().dispatcher(dispatcher).build()

        // Get an instance of Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(mockServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .callbackExecutor(currentThreadExecutor)
            .build()

        // Get an instance of blogService
        apiService = retrofit.create(ApiService::class.java)
        // Initialized repository
        dataSource = RemoteDataSource.getInstance(apiService)

        view = mock {}
        presenter = StopsAndRoutesPresenter(dataSource, view)
    }

    @After
    @Throws
    fun tearDown() {
        // We're done with tests, shut it down
        mockServer.shutdown()
    }

    @Test
    fun loadStopNamesTest() {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"stopNames\":[\"8 Maja\",\"AUCHAN\",\"Adamczewskich\",\"Adamieckiego\"]}")
        mockServer.enqueue(mockResponse)

        val inOrder = inOrder(view)
        presenter.loadStopNames()
        inOrder.verify(view, times(1)).showProgressBar()
        inOrder.verify(view, times(1)).hideProgressBar()
        inOrder.verify(view).displayStops(listOf("8 Maja", "AUCHAN", "Adamczewskich", "Adamieckiego"))
        verify(view, never()).reportThatSomethingWentWrong()
    }

    @Test
    fun loadStopNamesTest_404() {
        val mockResponse = MockResponse()
            .setResponseCode(404)
        mockServer.enqueue(mockResponse)

        val inOrder = inOrder(view)
        presenter.loadStopNames()
        inOrder.verify(view, times(1)).showProgressBar()
        inOrder.verify(view, times(1)).hideProgressBar()
        inOrder.verify(view, times(1)).reportThatSomethingWentWrong()
        verify(view, never()).displayStops(any())
    }

    @Test
    fun loadStopNamesTest_AlreadyPopulated() {
        presenter.setAllStopNames(listOf("8 Maja", "AUCHAN", "Adamczewskich", "Adamieckiego"))
        presenter.loadStopNames()
        verify(view, never()).showProgressBar()
        verify(view, never()).reportThatSomethingWentWrong()
        verify(view, never()).hideProgressBar()
        verify(view, never()).displayStops(any())
    }

    @Test
    fun queryTextChangedTest() {
        presenter.setAllStopNames(listOf("8 Maja", "AUCHAN", "Adamczewskich", "Adamieckiego"))
        presenter.queryTextChanged("a")
        verify(view).displayStops(listOf("AUCHAN", "Adamczewskich", "Adamieckiego"))
        verify(view, never()).showProgressBar()
        verify(view, never()).hideProgressBar()
        verify(view, never()).reportThatSomethingWentWrong()
    }

    @Test
    fun queryTextChangedTest_EmptyList() {
        presenter.setAllStopNames(emptyList())
        presenter.queryTextChanged("a")
        verify(view, never()).displayStops(any())
        verify(view, never()).showProgressBar()
        verify(view, never()).hideProgressBar()
        verify(view, never()).reportThatSomethingWentWrong()
        verify(view, times(1)).showStopNotFound()
    }

    @Test
    fun listItemClickedTest() {
        presenter.setShownStopNames(listOf("8 Maja", "AUCHAN", "Adamczewskich", "Adamieckiego"))
        presenter.listItemClicked(0)
        verify(view, times(1)).navigateToRouteVariants("8 Maja")
    }

    @Test
    fun listItemClickedTest_OutOfRange() {
        presenter.setShownStopNames(emptyList())
        presenter.listItemClicked(0)
        verify(view, never()).navigateToRouteVariants(any())
    }
}