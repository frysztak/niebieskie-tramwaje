package software.orpington.rozkladmpk.data.source

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import software.orpington.rozkladmpk.data.model.VehiclePositions

interface GPSService {
    @FormUrlEncoded
    @POST("position.php")
    fun getVehiclePosition(@Field("busList[bus][]") routeIDs: List<String>): Call<VehiclePositions>
}