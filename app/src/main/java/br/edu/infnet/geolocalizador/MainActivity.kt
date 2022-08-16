package br.edu.infnet.geolocalizador

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.requestLocationUpdates

class MainActivity : AppCompatActivity(), LocationListener{

    val LOCATION_PERMISSION_REQUEST = 11111
    val WRITE_REQUEST = 44444

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnObter = this.findViewById<Button>(R.id.btnObterLocal)
        btnObter.setOnClickListener {

            getLocationByNetwork()
            getLocationByGps()
        }

        val btnGravar = this.findViewById<Button>(R.id.btnGravar)
        btnGravar.setOnClickListener {

            gravarLocalizacao()
        }
    }


    fun getLocation(provider: String, permission: String): Location? {
        var location: Location? = null
        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        val isNetEnabled = locationManager.isProviderEnabled(provider)


        if (isNetEnabled) {

            Log.i("Alceu", "Net is enabled")
            if (ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(
                    provider,
                    2000L,
                    0f,
                    this)


                location =
                    locationManager.getLastKnownLocation(provider)

                }
            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.requestPermissions(
                        arrayOf(permission),
                        LOCATION_PERMISSION_REQUEST
                    )
                }
            }
        return location
        }


    private fun getLocationByNetwork() {
        var location: Location? = this.getLocation(LocationManager.NETWORK_PROVIDER, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (location != null) {
            Log.i("GetLocationByNetwork", "Location is not null")
            this.findViewById<TextView>(R.id.txtResultLatitude).setText(location.latitude.toString())
            this.findViewById<TextView>(R.id.txtResultLongitude).setText(location.longitude.toString())
        }
    }

    private fun getLocationByGps() {
        var location: Location? =
            this.getLocation(LocationManager.GPS_PROVIDER, Manifest.permission.ACCESS_FINE_LOCATION)
        if (location != null) {
            Log.i("GetLocationByGps", "Location is not null")
            this.findViewById<TextView>(R.id.txtResultLatitude)
                .setText(location.latitude.toString())
            this.findViewById<TextView>(R.id.txtResultLongitude)
                .setText(location.longitude.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationByNetwork()
        }
    }

    override fun onLocationChanged(p0: Location) {
    }

    private fun gravarLocalizacao() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("text/plain")
        intent.putExtra(Intent.EXTRA_TITLE, "local.txt")
        startActivityForResult(intent, WRITE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == WRITE_REQUEST && resultCode == RESULT_OK) {
            val fos = contentResolver.openOutputStream(data?.getData()!!)
            Log.i("DR4", "onActivityResult fos=" + fos.toString())

            val txtResultLatitude = this.findViewById<TextView>(R.id.txtResultLatitude).text.toString()
            val txtResultLongitude = this.findViewById<TextView>(R.id.txtResultLongitude).text.toString()

            val locationToSave = "Latitude = $txtResultLatitude, Longitude = $txtResultLongitude"
            Log.i("DR4", locationToSave)

            fos?.write(locationToSave.toByteArray())
            fos?.close()
            this.findViewById<TextView>(R.id.txtResultLatitude).setText(null)
            this.findViewById<TextView>(R.id.txtResultLongitude).setText(null)

        }
    }
}