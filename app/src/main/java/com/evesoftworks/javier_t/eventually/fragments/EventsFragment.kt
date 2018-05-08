package com.evesoftworks.javier_t.eventually.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
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
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.fragment_events.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventsFragment : Fragment(), EventListener<QuerySnapshot> {
    var lovedEvents: ArrayList<Event> = ArrayList()

    lateinit var adapterToListen: EventSectionAdapter
    var upcomingEvents: ArrayList<Event> = ArrayList()
    lateinit var docRef: Query
    var currentUserPreferences: ArrayList<String> = ArrayList()
    var favouritesEvents: ArrayList<Event> = ArrayList()
    var favouritesEventsToQuery: ArrayList<String> = ArrayList()
    var sections: ArrayList<EventSection> = ArrayList()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retrieveCurrentUserPreferencesAndFavourites()
        docRef = db.collection("Usuarios").whereEqualTo("photoId", FirebaseAuth.getInstance().currentUser!!.uid)
        docRef.addSnapshotListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.setHasFixedSize(true)
        adapterToListen = EventSectionAdapter(sections)

        swipe_refresh_events.setOnRefreshListener {
            refreshContent()
        }
        swipe_refresh_events.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onEvent(querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException?) {
        if (firebaseFirestoreException != null) {
            return
        }

        for (documentChanges in querySnapshot!!.documentChanges) {
            when (documentChanges.type) {
                DocumentChange.Type.ADDED -> {}
                DocumentChange.Type.MODIFIED -> {
                    retrieveCurrentUserPreferencesAndFavourites()
                }
                DocumentChange.Type.REMOVED -> {}
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    private fun refreshContent() {
        retrieveCurrentUserPreferencesAndFavourites()
    }

    private fun getLovedEvents() {
        lovedEvents.clear()

        db.collection("Eventos").orderBy("eventDate").limit(10).get().addOnCompleteListener { task ->
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

                    val event = Event(document.getString("eventId")!!, document.getString("category")!!, latLng!!, document.getString("name")!!, document.getString("description")!!, document.getString("placeId")!!, dateToString!!, tags.split(","))

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

        db.collection("Eventos").orderBy("eventDate").limit(10).get().addOnCompleteListener { task ->
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
                    val currentTime = Calendar.getInstance().timeInMillis

                    val diff = eventDate!!.time - currentTime

                    if (diff / (24 * 60 * 60 * 1000) in 0..4) {
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

    private fun getEventsInFavourites(favouritesEventsToQuery: ArrayList<String>) {
        favouritesEvents.clear()

        db.collection("Eventos").orderBy("eventDate").limit(10).get().addOnCompleteListener { task ->
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

                    for (eventId in favouritesEventsToQuery) {
                        if (eventId == event.eventId) {
                            favouritesEvents.add(event)
                        }
                    }
                }

                val layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
                recyclerView.layoutManager = layoutManager

                adapterToListen = EventSectionAdapter(sections)
                recyclerView.adapter = adapterToListen

                createFavouritesEventsSection()
            }
        }
    }

    private fun createLovedEventsSection() {
        sections.add(EventSection("A tu gusto", lovedEvents))
    }

    private fun createUpcomingEventsSection() {
        sections.add(EventSection("Llegan en breves", upcomingEvents))
    }

    private fun createFavouritesEventsSection() {
        sections.add(EventSection("En tus favoritos", favouritesEvents))
    }

    private fun retrieveCurrentUserPreferencesAndFavourites() {
        sections.clear()

        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.toObject(User::class.java)
                currentUserPreferences = user!!.categories
                favouritesEventsToQuery = user.eventsLiked

                getLovedEvents()
                getUpcomingEvents()
                getEventsInFavourites(favouritesEventsToQuery)

                swipe_refresh_events.isRefreshing = false
            }
        }
    }
}
