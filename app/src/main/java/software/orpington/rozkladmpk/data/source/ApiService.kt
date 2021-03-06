package software.orpington.rozkladmpk.data.source

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import software.orpington.rozkladmpk.data.model.*

interface ApiService {
    @GET("stops/and/routes")
    fun getStopsAndRoutes(): Call<StopsAndRoutes>

    @GET("route/{routeID}/info")
    fun getRouteInfo(@Path("routeID") routeID: String): Call<RouteInfo>

    @GET("route/{routeID}/directions")
    fun getRouteDirections(@Path("routeID") routeID: String): Call<RouteDirections>

    @GET("route/{routeID}/directions/through/{stopName}")
    fun getRouteDirectionsThroughStop(@Path("routeID") routeID: String,
                                      @Path("stopName") stopName: String): Call<RouteDirections>

    @GET("route/{routeID}/stops")
    fun getStopsForRoute(@Path("routeID") routeID: String): Call<Stops>

    @GET("routes/variants/stop/{stopName}")
    fun getRouteVariantsForStopName(@Path("stopName") stopName: String): Call<RouteVariants>

    @GET("route/{routeID}/timetable/at/{stopName}/direction/{direction}")
    fun getTimeTable(@Path("routeID") routeID: String,
                     @Path("stopName") stopName: String,
                     @Path("direction") direction: String): Call<TimeTable>

    @GET("trip/{tripID}/timeline")
    fun getTripTimeline(@Path("tripID") tripID: Int): Call<Timeline>

    @GET("route/{routeID}/map/at/{stopName}/direction/{direction}")
    fun getRouteMap(@Path("routeID") routeID: String,
                    @Path("stopName") stopName: String,
                    @Path("direction") direction: String): Call<MapData>

    @GET("trip/{tripID}/map")
    fun getTripMap(@Path("tripID") tripID: Int): Call<MapData>

    @GET("stops/{stopNames}/departures")
    fun getDepartures(@Path("stopNames") stopNames: String): Call<Departures>

    @GET("news/recent")
    fun getMostRecentNews(): Call<NewsItem>

    @GET("news/page/{pageNum}")
    fun getNews(@Path("pageNum") pageNum: Int): Call<List<NewsItem>>
}