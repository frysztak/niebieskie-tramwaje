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

@PowerMockIgnore("okhttp3.*")
@RunWith(PowerMockRunner::class)
@PrepareForTest(LoggerFactory::class)
class StopsAndRoutesPresenterTest {
    lateinit var dataSource: RemoteDataSource
    lateinit var mockServer: MockWebServer
    lateinit var apiService: ApiService
    lateinit var presenter: StopsAndRoutesPresenter
    lateinit var view: StopsAndRoutesContract.View

    companion object {
        lateinit var mockLogger: Logger
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
    }

    @Test
    fun loadStopNamesFailTest() {
        val mockResponse = MockResponse()
            .setResponseCode(404)
        mockServer.enqueue(mockResponse)

        val inOrder = inOrder(view)
        presenter.loadStopNames()
        verify(view, times(1)).reportThatSomethingWentWrong()
        inOrder.verify(view, times(1)).showProgressBar()
        inOrder.verify(view, times(1)).hideProgressBar()
    }
}