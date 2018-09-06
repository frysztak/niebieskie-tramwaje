package software.orpington.rozkladmpk

import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.data.source.ApiService
import software.orpington.rozkladmpk.data.source.RemoteDataSource
import okhttp3.OkHttpClient


object Injection {

    fun provideDataSource(httpClient: OkHttpClient, baseUrl: String = "http://88.198.99.142:8080"): RemoteDataSource {
        val apiService = ApiClient.get(httpClient, baseUrl).create(ApiService::class.java)
        return RemoteDataSource.getInstance(apiService)
    }
}

