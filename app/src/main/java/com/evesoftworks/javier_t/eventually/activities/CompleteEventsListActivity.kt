package com.evesoftworks.javier_t.eventually.activities

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.CompleteEventsAdapter
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataWithArgsListener
import com.evesoftworks.javier_t.eventually.utils.RecyclerItemDivider
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_complete_events_list.*
import kotlinx.android.synthetic.main.complete_events_list_toolbar.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CompleteEventsListActivity : AppCompatActivity(), OnRetrieveFirebaseDataWithArgsListener {
    var upcomingEvents: ArrayList<Event> = ArrayList()
    var lovedEvents: ArrayList<Event> = ArrayList()
    var currentUserPreferences: ArrayList<String> = ArrayList()
    var favouriteEvents: ArrayList<Event> = ArrayList()
    var favouritesEventsToQuery: ArrayList<String> = ArrayList()
    val db = FirebaseFirestore.getInstance()
    val onRetrieveFirebaseDataListener = this
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var adapter: CompleteEventsAdapter

    override fun onRetrieve(args: String?, arrayList: ArrayList<Event>?) {
        arrayList?.let {
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            complete_recycler.layoutManager = layoutManager

            adapter = CompleteEventsAdapter(it)
            complete_recycler.adapter = adapter

            complete_recycler.addItemDecoration(RecyclerItemDivider(this))

            swipeRefreshLayout.isRefreshing = false
            adapter.setAllItemsEnabled(!swipeRefreshLayout.isRefreshing)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_events_list)

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_complete)
        swipeRefreshLayout.setOnRefreshListener { refresh() }
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent)

        adapter = CompleteEventsAdapter(favouriteEvents)

        setSupportActionBar(complete_events_list_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        retrieveCurrentUserPreferencesAndFavourites()

    }

    private fun refresh() {
        retrieveCurrentUserPreferencesAndFavourites()
    }

    private fun checkEventSection() {
        val sectionTitle = intent.extras["sectionTitle"]

        supportActionBar?.title = sectionTitle.toString()

        when (sectionTitle) {
            getString(R.string.selected_for_you) -> {
                retrieveAllLovedEvents()
            }

            getString(R.string.upcoming_events) -> {
                retrieveAllUpcomingEvents()
            }

            getString(R.string.in_your_favs) -> {
                retrieveAllFavsEvents(favouritesEventsToQuery)
            }
        }
    }

    private fun retrieveAllLovedEvents() {
        lovedEvents.clear()

        db.collection("Eventos").orderBy("eventDate").get().addOnCompleteListener { task ->
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
                    val currentTime = Calendar.getInstance().timeInMillis

                    val diff = eventDate!!.time - currentTime

                    if (diff / (24 * 60 * 60 * 1000) < 0) {
                        for (i in 0 until currentUserPreferences.size) {
                            if (event.category == currentUserPreferences[i]) {
                                lovedEvents.add(event)
                            }
                        }
                    }
                }

                onRetrieveFirebaseDataListener.onRetrieve(arrayList = lovedEvents)
            }
        }
    }

    private fun retrieveAllUpcomingEvents() {
        upcomingEvents.clear()

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
                    val currentTime = Calendar.getInstance().timeInMillis

                    val diff = eventDate!!.time - currentTime

                    if (diff / (24 * 60 * 60 * 1000) in 0..4) {
                        upcomingEvents.add(event)
                    }
                }

                onRetrieveFirebaseDataListener.onRetrieve(arrayList = upcomingEvents)
            }
        }
    }

    private fun retrieveAllFavsEvents(favouriteEventsToQuery: ArrayList<String>) {
        favouriteEvents.clear()

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

                    for (eventId in favouriteEventsToQuery) {
                        if (eventId == event.eventId) {
                            favouriteEvents.add(event)
                        }
                    }
                }

                onRetrieveFirebaseDataListener.onRetrieve(arrayList = favouriteEvents)
            }
        }
    }

    private fun retrieveCurrentUserPreferencesAndFavourites() {
        adapter.setAllItemsEnabled(!swipeRefreshLayout.isRefreshing)

        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.toObject(User::class.java)
                currentUserPreferences = user!!.categories
                favouritesEventsToQuery = user.eventsLiked

                checkEventSection()
            }
        }
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
