package software.orpington.rozkladmpk

import software.orpington.rozkladmpk.data.source.ApiClient
import software.orpington.rozkladmpk.data.source.ApiService
import software.orpington.rozkladmpk.data.source.RemoteDataSource
import okhttp3.OkHttpClient


object Injection {

    private const val url = "https://orpington.software/apps/rozkladmpk/"

    fun provideDataSource(httpClient: OkHttpClient, baseUrl: String = url): RemoteDataSource {
        val apiService = ApiClient.get(httpClient, baseUrl).create(ApiService::class.java)
        return RemoteDataSource.getInstance(apiService)
    }
}

