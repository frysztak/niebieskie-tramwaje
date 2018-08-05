package com.orpington.software.rozkladmpk

import com.orpington.software.rozkladmpk.data.source.ApiClient
import com.orpington.software.rozkladmpk.data.source.ApiService
import com.orpington.software.rozkladmpk.data.source.RemoteDataSource
import com.orpington.software.rozkladmpk.data.source.RouteDetailsState
import java.io.File


class Injection {
    companion object {
        private var dataSource: RemoteDataSource? = null
        fun provideDataSource(cacheDir: File): RemoteDataSource {
            if (dataSource == null) {
                val apiService = ApiClient.get(cacheDir).create(ApiService::class.java)
                dataSource = RemoteDataSource.getInstance(apiService)
            }
            return dataSource!!
        }

        fun provideDataSource(): RemoteDataSource {
            if (dataSource != null) {
                return dataSource!!
            }
            throw Exception("Cache dir unknown")
        }

        private val routeDetailsState = RouteDetailsState()
        fun getRouteDetailsState(): RouteDetailsState {
            return routeDetailsState
        }
    }
}

