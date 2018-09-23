package software.orpington.rozkladmpk

import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.data.source.ApiService
import software.orpington.rozkladmpk.data.source.RemoteDataSource
import okhttp3.OkHttpClient
import software.orpington.rozkladmpk.data.source.GPSService


object Injection {

    private const val url = "http://192.168.0.22:8080/"
   // private const val url = "https://orpington.software/apps/rozkladmpk/"

    fun provideDataSource(httpClient: OkHttpClient, baseUrl: String = url): RemoteDataSource {
        val apiService = ApiClient.getMPK(httpClient, baseUrl).create(ApiService::class.java)
        val gpsService= ApiClient.getGPS(httpClient).create(GPSService::class.java)
        return RemoteDataSource.getInstance(apiService, gpsService)
    }
}

