package com.orpington.software.rozkladmpk

import com.orpington.software.rozkladmpk.data.source.ApiClient
import com.orpington.software.rozkladmpk.data.source.ApiService
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource


object Injection {

    fun provideDataSource(): RemoteDataSource {
        val apiService = ApiClient.client.create(ApiService::class.java)
        return RemoteDataSource.getInstance(apiService)
    }
}

