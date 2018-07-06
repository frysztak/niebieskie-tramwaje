package com.orpington.software.rozkladmpk.data.source.remote

import com.orpington.software.rozkladmpk.data.model.StopNames
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("stops")
    fun getStops(): Call<StopNames>
}