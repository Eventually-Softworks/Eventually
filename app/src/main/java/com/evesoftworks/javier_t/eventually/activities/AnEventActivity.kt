package com.evesoftworks.javier_t.eventually.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_an_event.*

class AnEventActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var supportMapFragment: SupportMapFragment

    override fun onMapReady(googleMap: GoogleMap?) {
        val bundle = intent.extras
        val event = bundle.getParcelable<Event>("anEvent")

        val currentLocLatLng = LatLng(event.latLng.latitude, event.latLng.longitude)

        googleMap!!.addMarker(MarkerOptions().position(currentLocLatLng))

        val cameraPosition: CameraPosition = CameraPosition.Builder()
                .target(currentLocLatLng)
                .zoom(17f)
                .bearing(90f)
                .tilt(30f)
                .build()

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_an_event)
        val bundle = intent.extras
        val event = bundle.getParcelable<Event>("anEvent")

        aneventtoolbar.title = event.name

        setSupportActionBar(aneventtoolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportMapFragment = map as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        aneventname.text = event.name
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return true
    }
}
