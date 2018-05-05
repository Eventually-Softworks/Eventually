package com.evesoftworks.javier_t.eventually.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.dbmodel.Category
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_an_event.*
import kotlinx.android.synthetic.main.activity_data_completion.*
import java.net.URI
import java.util.*

class AnEventActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var supportMapFragment: SupportMapFragment

    lateinit var mGeoDataClient: GeoDataClient
    lateinit var mPlaceDetectionClient: PlaceDetectionClient
    lateinit var event: Event
    lateinit var storageReference: StorageReference
    lateinit var bitmap: Bitmap

    override fun onMapReady(googleMap: GoogleMap?) {
        val currentLocLatLng = LatLng(event.latLng.latitude, event.latLng.longitude)

        googleMap!!.addMarker(MarkerOptions().position(currentLocLatLng))

        val cameraPosition: CameraPosition = CameraPosition.Builder()
                .target(currentLocLatLng)
                .zoom(17f)
                .bearing(90f)
                .tilt(30f)
                .build()

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.setAllGesturesEnabled(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_an_event)

        mGeoDataClient = Places.getGeoDataClient(this)
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this)

        val bundle = intent.extras
        event = bundle.getParcelable<Event>("anEvent")

        aneventtoolbar.title = event.name
        setSupportActionBar(aneventtoolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportMapFragment = map as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        aneventname.text = event.name
        setPlacePhoto()
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

    /*private fun getPlacePhotos() {
        mGeoDataClient.getPlacePhotos(event.placeId).addOnCompleteListener {
            val photos = it.result
            val photoMetadataBuffer = photos.photoMetadata
            val photoMetadata = photoMetadataBuffer.get(0)

            mGeoDataClient.getPhoto(photoMetadata).addOnCompleteListener {
                val photo = it.result
                bitmap = photo.bitmap
                setPlacePhoto()
            }
        }
    }*/

    private fun setPlacePhoto() {
        storageReference = FirebaseStorage.getInstance().reference.child("eventsphotos/${event.name}.jpg")

        storageReference.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(aneventimage)
        }
    }
}
