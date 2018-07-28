package com.orpington.software.rozkladmpk.data.source

import com.orpington.software.rozkladmpk.data.model.RouteInfo
import com.orpington.software.rozkladmpk.data.model.RouteVariants
import com.orpington.software.rozkladmpk.data.model.StopNames
import com.orpington.software.rozkladmpk.data.model.TimeTable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("stops")
    fun getStops(): Call<StopNames>

    @GET("route/{routeID}/info")
    fun getRouteInfo(@Path("routeID") routeID: String): Call<RouteInfo>

    @GET("routes/variants/stop/{stopName}")
    fun getRouteVariantsForStopName(@Path("stopName") stopName: String): Call<RouteVariants>

    @GET("route/{routeID}/timetable/at/{atStopName}/from/{fromStopName}/to/{toStopName}")
    fun getTimeTable(@Path("routeID") routeID: String,
                     @Path("atStopName") atStopName: String,
                     @Path("fromStopName") fromStopName: String,
                     @Path("toStopName") toStopName: String): Call<TimeTable>
}