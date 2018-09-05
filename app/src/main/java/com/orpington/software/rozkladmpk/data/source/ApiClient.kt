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
        private var sRetrofit: Retrofit? = null
        private var okHttpClient: OkHttpClient? = null

        fun getHttpClient(cacheDir: File): OkHttpClient {
            if (okHttpClient == null) {
                synchronized(OkHttpClient::class.java) {
                    val cacheSize: Long = 5 * 1024 * 1024 // 5 MB
                    val cache = Cache(cacheDir, cacheSize)

                    val interceptor = HttpLoggingInterceptor()
                    interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

                    okHttpClient = OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .cache(cache)
                        .build()
                }
            }
            return okHttpClient!!
        }

        fun get(client: OkHttpClient, baseUrl: String): Retrofit {
            if (sRetrofit == null) {
                synchronized(Retrofit::class.java) {
                    if (sRetrofit == null) {
                        sRetrofit = Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addConverterFactory(MoshiConverterFactory.create())
                            .client(client)
                            .build()
                    }
                }
            }
            return sRetrofit!!
        }
    }

}

