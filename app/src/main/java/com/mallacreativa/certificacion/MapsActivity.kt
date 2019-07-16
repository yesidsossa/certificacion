package com.mallacreativa.certificacion

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

abstract class MapsActivity : AppCompatActivity(), OnMapReadyCallback, PlaceSelectionListener {

    private lateinit var mMap: GoogleMap
    private lateinit var locationListener: InDriveLocationListener
    private lateinit var locationManager: LocationManager
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val GOOGLE_BASE_URL = "https://maps.googleapis.com/"
    private val POLYLINE_CACHE_KEY = "polyline"


    private val REQUEST_ACCESS_PERMISSION = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        Places.initialize(applicationContext, "AIzaSyAF7ycjJe_F8sfUiDZVd_8tpMp2C72Mvrs")

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        getUserLocation()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment!!.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME))
        autocompleteFragment.setOnPlaceSelectedListener(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(
            MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney")
            .icon(bitmapDescriptorFromVector(this,R.drawable.marker)))

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.uiSettings.isZoomControlsEnabled = true

        reloadMapPosition(locationListener.mylocation?.latitude, locationListener.mylocation?.longitude)

    }

    override fun onPlaceSelected(p0: Place) {

        createMountDialog(p0).show()

    }

    override fun onError(p0: Status) {
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(0, 0, 100, 100)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), REQUEST_ACCESS_PERMISSION
            )
        } else {
            requestUserLocation()
        }
    }

    @SuppressLint("MissingPermission")
    fun requestUserLocation() {
        locationListener = InDriveLocationListener()
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.1f, locationListener)
    }
    private fun reloadMapPosition(latitude: Double?, longitude: Double?) {
        val lat: Double = latitude ?: 0.0
        val lon: Double = longitude ?: 0.0

        val pos = LatLng(lat,lon)
        this.mMap.addMarker(MarkerOptions().position(pos).title("My position"))
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(pos))
        this.mMap.setMinZoomPreference(5f)
    }

    private fun saveUserRoute(userId: String, route: MapRoute) {
        database.child("addresess").child("address_$userId").setValue(route)
    }

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }

    private fun connectPolyline(polylines: List<LatLng>) {
        val polylineOptions = PolylineOptions().addAll(polylines).clickable(true)
        mMap.addPolyline(polylineOptions)
        listenMyService(auth.currentUser!!.uid)
    }

    private fun createMountDialog(place: Place): Dialog {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val viewInflated = inflater.inflate(R.layout.abc_action_bar_up_container, null)

        dialogBuilder.setView(viewInflated)
            .setPositiveButton("save", DialogInterface.OnClickListener { dialog, which ->
                saveRoute(place, 6)
            })
            .setNegativeButton("cancel", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })

        return dialogBuilder.create()
    }

    private fun saveRoute(place: Place, serviceMount: Int) {
        val route = MapRoute()
        route.origin_lat = locationListener.mylocation?.latitude.toString()
        route.origin_long = locationListener.mylocation?.longitude.toString()
        route.destination_lat = place.latLng!!.latitude.toString()
        route.destination_long = place.latLng!!.longitude.toString()
        route.username = auth.currentUser!!.email!!
        route.service_demand = MountDemand(serviceMount)

        saveUserRoute(auth.currentUser!!.uid, route)
        paintRoute(route)
    }

    private fun paintRoute(route: MapRoute) {

        if (paintPolylineFromCache()) {
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(GOOGLE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(DirectionsService::class.java)
        val call = service.getDirection("${route.origin_lat},${route.origin_long}", "${route.destination_lat}, ${route.destination_long}")
        call.enqueue(object: Callback<DirectionsService.Direction> {
            override fun onResponse(call: Call<DirectionsService.Direction>, response: Response<DirectionsService.Direction>) {
                if(response.code() == 200){
                    val polylineDecoded = decodePoly(response.body()!!.routes[0].polyline.points)
                    savePolylineInCache(response.body()!!.routes[0].polyline.points)

                    connectPolyline(polylineDecoded)
                }
            }

            override fun onFailure(call: Call<DirectionsService.Direction>, t: Throwable) {
                Toast.makeText(applicationContext,"Failed Load Direction",Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun listenMyService(userId: String) {
        database.child("addresess").child("address_$userId").addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val myRoute = snapshot.getValue(DirectionsService.MyRoute::class.java)

                if(myRoute!!.status) {

                    database.child("drivers").child("driver_${myRoute.drivername}").addValueEventListener(object: ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {}

                        override fun onDataChange(dsnapshot: DataSnapshot) {
                            val driverAssigned =  dsnapshot.getValue(DirectionsService.Driver::class.java)

                            if (driverAssigned != null) {
                                paintIcon(driverAssigned!!.name, driverAssigned.origin_lat.toDouble(), driverAssigned.origin_long.toDouble())
                            }
                        }

                    })
                } else {
                    clearIcon()
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun paintIcon(driver: String, lat: Double, lon: Double) {
        clearIcon()
        paintPolylineFromCache()

        val markerOptions = MarkerOptions()
            .position(LatLng(lat, lon))
            .title(driver)
            .icon(bitmapDescriptorFromVector(this, R.drawable.marker))

        mMap.addMarker(markerOptions)
    }
    private fun clearIcon() {
        mMap.clear()
    }
    private fun paintPolylineFromCache(): Boolean {


        return false
    }

    private fun savePolylineInCache(polyline: String) {

    }

    inner class InDriveLocationListener : LocationListener {
        var mylocation: Location?

        constructor() : super() {
            mylocation = Location("me")
            mylocation!!.longitude
            mylocation!!.latitude
        }

        override fun onLocationChanged(location: Location?) {
            mylocation = location
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

        override fun onProviderEnabled(p0: String?) {}

        override fun onProviderDisabled(p0: String?) {}
    }

    inner class MapRoute {
        var drivername: String = "nil"
        var destination_lat: String = ""
        var destination_long: String = ""
        var origin_lat: String = ""
        var origin_long: String = ""
        var status: Boolean = false
        var username: String = ""
        lateinit var service_demand: MountDemand
    }

    inner class MountDemand {
        var drivername = "nil"
        var service_mount = 0

        constructor(mount: Int) {
            service_mount = mount
        }
    }



}
