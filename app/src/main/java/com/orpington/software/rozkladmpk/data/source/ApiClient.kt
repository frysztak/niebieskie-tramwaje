package com.orpington.software.rozkladmpk.data.source

import com.orpington.software.rozkladmpk.BuildConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

class ApiClient {
    companion object {
        private val BASE_API_URL = "http://88.198.99.142:8080"

        private var sRetrofit: Retrofit? = null

        fun get(cacheDir: File): Retrofit {
            if (sRetrofit == null) {
                synchronized(Retrofit::class.java) {
                    if (sRetrofit == null) {
                        val cacheSize: Long = 5 * 1024 * 1024 // 5 MB
                        val cache = Cache(cacheDir, cacheSize)

                        val interceptor = HttpLoggingInterceptor()
                        interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

                        val okHttpClient = OkHttpClient.Builder()
                            .addInterceptor(interceptor)
                            .cache(cache)
                            .build()

                        sRetrofit = Retrofit.Builder()
                            .baseUrl(BASE_API_URL)
                            .addConverterFactory(MoshiConverterFactory.create())
                            .client(okHttpClient)
                            .build()
                    }
                }
            }
            return sRetrofit!!
        }
    }

}

