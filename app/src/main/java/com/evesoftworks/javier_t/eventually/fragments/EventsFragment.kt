package com.evesoftworks.javier_t.eventually.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.EventSectionAdapter
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.evesoftworks.javier_t.eventually.dbmodel.EventSection
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.fragment_events.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventsFragment : Fragment() {
    var lovedEvents: ArrayList<Event> = ArrayList()
    var upcomingEvents: ArrayList<Event> = ArrayList()
    var currentUserPreferences: ArrayList<String> = ArrayList()
    var suggestedEvents: ArrayList<Event> = ArrayList()
    var sections: ArrayList<EventSection> = ArrayList()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retrieveCurrentUserPreferences()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.setHasFixedSize(true)

        lovedEvents = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    private fun getLovedEvents() {
        lovedEvents.clear()

        db.collection("Eventos").limit(10).orderBy("eventDate").get().addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                for (document in task.result) {
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

                    val event = Event(document.getString("category")!!, latLng!!, document.getString("name")!!, document.getString("description")!!, document.getString("placeId")!!, dateToString!!, tags.split(","))

                    for (i in 0 until currentUserPreferences.size) {
                        if (event.category == currentUserPreferences[i]) {
                            lovedEvents.add(event)
                        }
                    }
                }

                val layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
                recyclerView.layoutManager = layoutManager

                val adapter = EventSectionAdapter(sections)
                recyclerView.adapter = adapter

                createLovedEventsSection()
            }
        }
    }

    private fun getUpcomingEvents() {
        upcomingEvents.clear()

        db.collection("Eventos").limit(10).orderBy("eventDate").get().addOnCompleteListener{ task ->
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

                    val event = Event(document.getString("category")!!, latLng!!, document.getString("name")!!, document.getString("description")!!, document.getString("placeId")!!, dateToString!!, tags.split(","))
                    val currentTime = Calendar.getInstance().timeInMillis

                    val diff = eventDate!!.time - currentTime

                    if (diff / (24 * 60 * 60 * 1000) < 4) {
                        upcomingEvents.add(event)
                    }
                }

                val layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
                recyclerView.layoutManager = layoutManager

                val adapter = EventSectionAdapter(sections)
                recyclerView.adapter = adapter

                createUpcomingEventsSection()
            }
        }
    }

    private fun getSuggestedEvents() {

    }

    private fun createLovedEventsSection() {
        sections.add(EventSection("Te encantarán", lovedEvents))
    }

    private fun createUpcomingEventsSection() {
        sections.add(EventSection("Próximamente", upcomingEvents))
    }

    private fun retrieveCurrentUserPreferences() {
        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.toObject(User::class.java)
                currentUserPreferences = user!!.categories

                getLovedEvents()
                getUpcomingEvents()
            }
        }
    }
}
