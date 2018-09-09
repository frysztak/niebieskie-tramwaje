package software.orpington.rozkladmpk.data.source

import software.orpington.rozkladmpk.data.model.*
import software.orpington.rozkladmpk.data.source.IDataSource.LoadDataCallback


interface IRemoteDataSource : IDataSource {
    fun getStopsAndRoutes(callback: LoadDataCallback<StopsAndRoutes>)

    fun getRouteInfo(routeID: String, callback: LoadDataCallback<RouteInfo>)
    fun getRouteDirections(routeID: String, callback: LoadDataCallback<RouteDirections>)
    fun getRouteDirectionsThroughStop(routeID: String, stopName: String, callback: LoadDataCallback<RouteDirections>)
    fun getStopsForRoute(routeID: String, callback: LoadDataCallback<Stops>)

    fun getRouteVariantsForStopName(stopName: String, callback: LoadDataCallback<RouteVariants>)

    fun getTimeTable(routeID: String, stopName: String, direction: String, callback: LoadDataCallback<TimeTable>)

    fun getTripTimeline(tripID: Int, callback: LoadDataCallback<Timeline>)

    fun getRouteShapes(routeID: String, stopName: String, direction: String, callback: LoadDataCallback<Shapes>)
}