package com.evesoftworks.javier_t.eventually.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.EventsAdapter
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.evesoftworks.javier_t.eventually.dbmodel.EventSection
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataListener
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.edit_profile_toolbar.*
import java.text.SimpleDateFormat
import java.util.*

class UserProfileActivity : AppCompatActivity(), OnRetrieveFirebaseDataListener {
    val db = FirebaseFirestore.getInstance()
    val onRetrieveFirebaseDataListener = this
    var confirmedAssistanceEvents: ArrayList<Event> = ArrayList()
    lateinit var adapter: EventsAdapter
    var confirmedAssistanceEventsId: ArrayList<String> = ArrayList()
    var sections: ArrayList<EventSection> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        adapter = EventsAdapter(confirmedAssistanceEvents)

        setSupportActionBar(editToolbar)
        editToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        editToolbar.setNavigationOnClickListener { finish() }

        getUserInfo()
        retrieveConfirmedAssistanceEventsId()
    }

    override fun onRetrieved() {
        getConfirmedAssistanceEvents()
    }

    private fun getUserInfo() {
        val retrievedUserData = intent.extras
        val arrayOfUserData = retrievedUserData.getStringArrayList("USERDATA")

        Picasso.get().load(FirebaseAuth.getInstance().currentUser!!.photoUrl).into(profile_my_pic)
        profile_my_name.setText(arrayOfUserData[0], TextView.BufferType.EDITABLE)
        profile_my_email.setText(arrayOfUserData[1], TextView.BufferType.EDITABLE)
        profile_my_username.setText(arrayOfUserData[2], TextView.BufferType.EDITABLE)
    }

    private fun userIsEditing() {

    }

    private fun getConfirmedAssistanceEvents() {
        db.collection("Eventos").orderBy("eventDate").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    val geoPoint: GeoPoint? = document.getGeoPoint("latLng")
                    val eventDate: Date? = document.getDate("eventDate")
                    var latLng: LatLng? = null
                    var dateToString: String? = null
                    val tags: String = document.get("tags").toString()
                    val spanishLocale = Locale("es", "ES")
                    val simpleDateFormat = SimpleDateFormat("dd MMMM yyy HH:mm", spanishLocale)
                    simpleDateFormat.timeZone = TimeZone.getTimeZone("Europe/Madrid")

                    geoPoint?.let {
                        latLng = LatLng(it.latitude, it.longitude)
                    }

                    eventDate?.let {
                        dateToString = simpleDateFormat.format(it)
                    }

                    val event = Event(document.getString("eventId")!!, document.getString("category")!!, latLng!!, document.getString("name")!!, document.getString("description")!!, document.getString("placeId")!!, dateToString!!, tags.split(","))

                    for (assistingEventId in confirmedAssistanceEventsId) {
                        if (assistingEventId == event.eventId) {
                            confirmedAssistanceEvents.add(event)
                        }
                    }
                }

                confirmed_assistance_recycle.setHasFixedSize(true)

                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                confirmed_assistance_recycle.layoutManager = layoutManager

                confirmed_assistance_recycle.adapter = adapter
            }
        }
    }

    private fun retrieveConfirmedAssistanceEventsId() {
        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.toObject(User::class.java)
                confirmedAssistanceEventsId = user!!.eventsAssisting

                onRetrieveFirebaseDataListener.onRetrieved()
            }
        }
    }
}
