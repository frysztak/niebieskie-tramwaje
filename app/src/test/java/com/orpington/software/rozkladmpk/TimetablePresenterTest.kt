package com.orpington.software.rozkladmpk

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.orpington.software.rozkladmpk.data.source.ApiService
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import com.orpington.software.rozkladmpk.timetable.TimetableContract
import com.orpington.software.rozkladmpk.timetable.TimetablePresenter
import com.orpington.software.rozkladmpk.utils.CurrentThreadExecutor
import okhttp3.OkHttpClient
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
import java.util.*

@PowerMockIgnore("okhttp3.*", "retrofit2.*")
@RunWith(PowerMockRunner::class)
@PrepareForTest(LoggerFactory::class)
class TimetablePresenterTest {
    private lateinit var dataSource: RemoteDataSource
    private lateinit var mockServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var presenter: TimetablePresenter
    private lateinit var view: TimetableContract.View

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
        presenter = TimetablePresenter(dataSource, view)
    }

    @After
    @Throws
    fun tearDown() {
        // We're done with tests, shut it down
        mockServer.shutdown()
    }

    @Test
    fun getCurrentDayType_Weekday() {
        val calendar = mock<Calendar> {
            on { get(Calendar.DAY_OF_WEEK) } doReturn Calendar.MONDAY
        }
    }
}