package com.mallacreativa.certificacion

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsService {

    @GET("maps/api/directions/json?key=AIzaSyAF7ycjJe_F8sfUiDZVd_8tpMp2C72Mvrs")
    fun getDirection(@Query("origin") origin: String, @Query("destination") destination: String): Call<Direction>

    class Direction {
        @SerializedName("routes")
        var routes = ArrayList<Route>()
    }

    class Route {
        @SerializedName("overview_polyline")
        var polyline = Polyline()
    }

    class Polyline {
        @SerializedName("points")
        var points: String = ""
    }

    class MyRoute {
        var drivername: String = "nil"
        var status: Boolean = false
    }

    class Driver {
        var name = ""
        var origin_lat = ""
        var origin_long = ""
    }
}