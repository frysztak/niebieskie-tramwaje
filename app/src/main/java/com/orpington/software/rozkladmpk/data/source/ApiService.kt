package com.orpington.software.rozkladmpk.data.source

import com.orpington.software.rozkladmpk.data.model.RouteVariants
import com.orpington.software.rozkladmpk.data.model.StopNames
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("stops")
    fun getStops(): Call<StopNames>

    @GET("routes/variants/stop/{stopName}")
    fun getRouteVariantsForStopName(@Path("stopName") stopName: String): Call<RouteVariants>
}