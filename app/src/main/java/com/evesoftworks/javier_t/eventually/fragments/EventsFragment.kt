package com.evesoftworks.javier_t.eventually.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.EventSectionAdapter
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.evesoftworks.javier_t.eventually.dbmodel.EventSection
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataListener
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventsFragment : Fragment(), EventListener<QuerySnapshot>, OnRetrieveFirebaseDataListener {
    var lovedEvents: ArrayList<Event> = ArrayList()
    lateinit var adapterToListen: EventSectionAdapter
    var upcomingEvents: ArrayList<Event> = ArrayList()
    lateinit var docRef: Query
    val onRetrieveFirebaseDataListener = this
    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
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

    override fun onRetrieved() {
        createLovedEventsSection()
        createUpcomingEventsSection()
        createFavouritesEventsSection()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView = activity!!.findViewById(R.id.recyclerView)

        recyclerView.setHasFixedSize(true)
        swipeRefreshLayout = activity!!.findViewById(R.id.swipe_refresh_events)

        adapterToListen = EventSectionAdapter(sections)

        swipeRefreshLayout.setOnRefreshListener { refreshContent() }
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent)

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
                DocumentChange.Type.ADDED -> {
                }
                DocumentChange.Type.MODIFIED -> {
                    retrieveCurrentUserPreferencesAndFavourites()
                }
                DocumentChange.Type.REMOVED -> {
                }
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

        db.collection("Eventos").whereGreaterThanOrEqualTo("eventDate", Calendar.getInstance().time).orderBy("eventDate").get().addOnCompleteListener { task ->
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

                    if (lovedEvents.size <= 6) {
                        for (i in 0 until currentUserPreferences.size) {
                            if (event.category.toLowerCase() == currentUserPreferences[i].toLowerCase()) {
                                lovedEvents.add(event)
                            }
                        }
                    }
                }

                val layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
                recyclerView.layoutManager = layoutManager

                val adapter = EventSectionAdapter(sections)
                recyclerView.adapter = adapter
            }
        }
    }

    private fun getUpcomingEvents() {
        upcomingEvents.clear()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 4)

        val rangeDate = calendar.time

        db.collection("Eventos").whereGreaterThanOrEqualTo("eventDate", Calendar.getInstance().time).whereLessThan("eventDate", rangeDate).orderBy("eventDate").limit(6).get().addOnCompleteListener { task ->
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

                    upcomingEvents.add(event)
                }

                val layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
                recyclerView.layoutManager = layoutManager

                val adapter = EventSectionAdapter(sections)
                recyclerView.adapter = adapter
            }
        }
    }

    private fun getEventsInFavourites(favouritesEventsToQuery: ArrayList<String>) {
        favouritesEvents.clear()

        db.collection("Eventos").orderBy("name").get().addOnCompleteListener { task ->
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

                    if (favouritesEvents.size <= 6) {
                        for (eventId in favouritesEventsToQuery) {
                            if (eventId == event.eventId) {
                                favouritesEvents.add(event)
                            }
                        }
                    }
                }

                val layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
                recyclerView.layoutManager = layoutManager

                adapterToListen = EventSectionAdapter(sections)
                recyclerView.adapter = adapterToListen
            }
        }
    }

    private fun createLovedEventsSection() {
        sections.add(EventSection(getString(R.string.selected_for_you), lovedEvents))
    }

    private fun createUpcomingEventsSection() {
        sections.add(EventSection(getString(R.string.upcoming_events), upcomingEvents))
    }

    private fun createFavouritesEventsSection() {
        sections.add(EventSection(getString(R.string.in_your_favs), favouritesEvents))
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

                onRetrieveFirebaseDataListener.onRetrieved()

                swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}
