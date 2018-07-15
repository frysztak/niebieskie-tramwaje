package com.orpington.software.rozkladmpk

import com.orpington.software.rozkladmpk.data.source.ApiClient
import com.orpington.software.rozkladmpk.data.source.ApiService
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import java.io.File


object Injection {

    fun provideDataSource(cacheDir: File): RemoteDataSource {
        val apiService = ApiClient.get(cacheDir).create(ApiService::class.java)
        return RemoteDataSource.getInstance(apiService)
    }
}

