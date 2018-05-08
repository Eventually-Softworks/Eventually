package com.evesoftworks.javier_t.eventually.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.OnEventStateChangedListener
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataListener
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.varunest.sparkbutton.SparkEventListener
import kotlinx.android.synthetic.main.activity_an_event.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AnEventActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener, OnEventStateChangedListener, OnRetrieveFirebaseDataListener {
    lateinit var supportMapFragment: SupportMapFragment

    lateinit var mGeoDataClient: GeoDataClient
    lateinit var mPlaceDetectionClient: PlaceDetectionClient
    lateinit var event: Event
    var dynamicLink: String? = null
    var assistingFound: Boolean = false
    var onEventStateChangedListener: OnEventStateChangedListener = this
    var onRetrieveFirebaseDataListener: OnRetrieveFirebaseDataListener = this
    lateinit var eventsLikedToPush: ArrayList<String>
    lateinit var eventsAssistingToPush: ArrayList<String>
    lateinit var storageReference: StorageReference
    val db = FirebaseFirestore.getInstance()
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.fab_event_share -> {
                dynamicLink?.let {
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_TEXT, "¿Te vienes conmigo? $it")
                    intent.type = "text/plain"
                    startActivity(intent)
                }
            }

            R.id.assistance_button -> {
                checkIfEventIsAlreadyInFavouritesAndAssistance(event.eventId)
                actionsToEventsAsssistingList(assistingFound)
            }
        }
    }

    override fun onRetrieved() {
        prepareData()
    }

    override fun onTaskResultGiven(boolean: Boolean) {
        if (boolean) {
            assistance_button.background = ContextCompat.getDrawable(this, R.drawable.rounded_button_cancel)
            assistance_button.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            assistance_button.text = getString(R.string.assistance_cancel)
        } else {
            assistance_button.background = ContextCompat.getDrawable(this, R.drawable.rounded_button)
            assistance_button.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            assistance_button.text = getString(R.string.event_assist)
        }
    }

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

        retrieveEventsListFromCurrentUser()

        mGeoDataClient = Places.getGeoDataClient(this)
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this)

        val bundle = intent.extras
        checkFromWheresEventInfoComing(bundle)

        setSupportActionBar(aneventtoolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        spark_fav.setEventListener(object : SparkEventListener {
            override fun onEventAnimationEnd(button: ImageView?, buttonState: Boolean) {}

            override fun onEventAnimationStart(button: ImageView?, buttonState: Boolean) {}

            override fun onEvent(button: ImageView?, buttonState: Boolean) {
                actionsToEventsLikedList(buttonState)
                spark_fav.playAnimation()
                spark_fav.isChecked = buttonState
            }
        })

        fab_event_share.setOnClickListener(this)
        assistance_button.setOnClickListener(this)
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

    private fun setPlacePhoto() {
        storageReference = FirebaseStorage.getInstance().reference.child("eventsphotos/${event.eventId}.jpg")

        storageReference.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(aneventimage)
        }
    }

    private fun checkIfEventIsAlreadyInFavouritesAndAssistance(eventId: String) {
        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.toObject(User::class.java)

                for (event in user!!.eventsLiked) {
                    if (event == eventId) {
                        spark_fav.isChecked = true
                    }
                }

                for (event in user.eventsAssisting) {
                    if (event == eventId) {
                        assistingFound = true
                    }
                }

                onEventStateChangedListener.onTaskResultGiven(assistingFound)
            }
        }
    }

    private fun retrieveEventsListFromCurrentUser() {
        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.toObject(User::class.java)
                eventsLikedToPush = user!!.eventsLiked
                eventsAssistingToPush = user.eventsAssisting
            }
        }
    }

    private fun actionsToEventsLikedList(state: Boolean) {
        if (state) {
            eventsLikedToPush.add(event.eventId)
        } else {
            eventsLikedToPush.remove(event.eventId)
        }

        performUpdate("eventsLiked", eventsLikedToPush)
    }

    private fun actionsToEventsAsssistingList(state: Boolean) {
        if (state) {
            eventsAssistingToPush.remove(event.eventId)
            assistingFound = false
        } else {
            eventsAssistingToPush.add(event.eventId)
        }

        performUpdate("eventsAssisting", eventsAssistingToPush)
    }

    private fun performUpdate(eventField: String, eventList: ArrayList<String>) {
        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).update(eventField, eventList)
    }

    private fun generateDynamicLink() {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://evedb-98c72.firebaseapp.com/${event.eventId}"))
                .setDynamicLinkDomain("jh248.app.goo.gl")
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder()
                        .setFallbackUrl(Uri.parse("https://evedb-98c72.firebaseapp.com"))
                        .build())
                .buildShortDynamicLink().addOnCompleteListener {
                    if (it.isSuccessful) {
                        dynamicLink = it.result.shortLink.toString()
                    }
                }
    }

    private fun prepareData() {
        generateDynamicLink()
        checkIfEventIsAlreadyInFavouritesAndAssistance(event.eventId)
        aneventtoolbar.title = event.name
        aneventname.text = event.name
        aneventtime.text = event.eventDate
        aneventdescription.text = event.description
        setPlacePhoto()
        supportMapFragment = map as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }

    private fun checkFromWheresEventInfoComing(bundle: Bundle) {
        if (bundle.get("DYN_LINK") != null) {
            val eventId: String = bundle.getString("DYN_LINK")

            db.collection("Eventos").document(eventId).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result

                    val geoPoint: GeoPoint? = document.getGeoPoint("latLng")
                    val eventDate: Date? = document.getDate("eventDate")
                    var latLng: LatLng? = null
                    var dateToString: String? = null
                    val tags: String = document.get("tags").toString()

                    geoPoint?.let {
                        latLng = LatLng(it.latitude, it.longitude)
                    }

                    eventDate?.let {
                        val spanishLocale = Locale("es", "ES")
                        val simpleDateFormat = SimpleDateFormat("dd MMMM yyy HH:mm", spanishLocale)
                        simpleDateFormat.timeZone = TimeZone.getTimeZone("Europe/Madrid")
                        dateToString = simpleDateFormat.format(it)
                    }

                    event = Event(document.getString("eventId")!!, document.getString("category")!!, latLng!!, document.getString("name")!!, document.getString("description")!!, document.getString("placeId")!!, dateToString!!, tags.split(","))

                    onRetrieveFirebaseDataListener.onRetrieved()
                }
            }
        } else {
            event = bundle.getParcelable("anEvent")
            prepareData()
        }
    }
}
