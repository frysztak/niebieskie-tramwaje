package com.orpington.software.rozkladmpk.data.source

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient


class ApiClient {
    companion object {
        private val BASE_API_URL = "http://192.168.0.21:8080"

        private var sRetrofit: Retrofit? = null

        val client: Retrofit
            get() {
                if (sRetrofit == null) {
                    synchronized(Retrofit::class.java) {
                        if (sRetrofit == null) {
                            //val interceptor = HttpLoggingInterceptor()
                            //interceptor.setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
                            //val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
                            val client = OkHttpClient.Builder().build()
                            sRetrofit = Retrofit.Builder()
                                .baseUrl(BASE_API_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(client)
                                .build()
                        }
                    }
                }
                return sRetrofit!!
            }
    }

}

