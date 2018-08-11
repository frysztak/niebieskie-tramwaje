package com.orpington.software.rozkladmpk.data.source

import com.orpington.software.rozkladmpk.BuildConfig
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import se.ansman.kotshi.KotshiJsonAdapterFactory
import java.io.File

class ApiClient {
    companion object {
        private val BASE_API_URL = "http://192.168.0.22:8080"

        private var sRetrofit: Retrofit? = null

        fun get(cacheDir: File): Retrofit {
            if (sRetrofit == null) {
                synchronized(Retrofit::class.java) {
                    if (sRetrofit == null) {
                        val cacheSize: Long = 10 * 1024 * 1024 // 10 MB
                        val cache = Cache(cacheDir, cacheSize)

                        val interceptor = HttpLoggingInterceptor()
                        interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

                        val okHttpClient = OkHttpClient.Builder()
                            .addInterceptor(interceptor)
                            .cache(cache)
                            .build()

                        val moshi = Moshi.Builder().add(ApplicationJsonAdapterFactory.INSTANCE).build()
                        sRetrofit = Retrofit.Builder()
                            .baseUrl(BASE_API_URL)
                            .addConverterFactory(MoshiConverterFactory.create(moshi))
                            .client(okHttpClient)
                            .build()
                    }
                }
            }
            return sRetrofit!!
        }
    }

}

@KotshiJsonAdapterFactory
abstract class ApplicationJsonAdapterFactory : JsonAdapter.Factory {
    companion object {
        val INSTANCE: ApplicationJsonAdapterFactory = KotshiApplicationJsonAdapterFactory()
    }
}
