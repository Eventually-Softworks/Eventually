package com.evesoftworks.javier_t.eventually.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.CompleteEventsAdapter
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataListener
import com.evesoftworks.javier_t.eventually.utils.RecyclerItemDivider
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.activity_events_schedule.*
import kotlinx.android.synthetic.main.month_toolbar.*
import kotlinx.android.synthetic.main.schedule_toolbar.*
import java.text.SimpleDateFormat
import java.util.*

class EventsScheduleActivity : AppCompatActivity(), OnRetrieveFirebaseDataListener, CompactCalendarView.CompactCalendarViewListener, EventListener<QuerySnapshot> {
    val dateFormatForMonth = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    var confirmedAssistanceEventsId: ArrayList<String> = ArrayList()
    var confirmedAssistanceEvents: ArrayList<Event> = ArrayList()
    val db = FirebaseFirestore.getInstance()
    val onRetrieveFirebaseDataListener = this
    lateinit var docRef: Query

    override fun onRetrieved() {
        getConfirmedAssistanceEvents()
    }

    override fun onDayClick(dateClicked: Date?) {
        getEventsWithSpecifiedDate(dateClicked!!)
    }

    override fun onMonthScroll(firstDayOfNewMonth: Date?) {
        month_toolbar.title = dateFormatForMonth.format(firstDayOfNewMonth)
    }

    override fun onEvent(querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException?) {
        if (firebaseFirestoreException != null) {
            return
        }

        for (documentChanges in querySnapshot!!.documentChanges) {
            when (documentChanges.type) {
                DocumentChange.Type.ADDED -> {}
                DocumentChange.Type.MODIFIED -> {
                    retrieveInfoAboutCurrentUser()
                }
                DocumentChange.Type.REMOVED -> {}
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_schedule)

        docRef = db.collection("Usuarios").whereEqualTo("photoId", FirebaseAuth.getInstance().currentUser!!.uid)
        docRef.addSnapshotListener(this)

        setSupportActionBar(schedule_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        prepareCalendar()
        retrieveInfoAboutCurrentUser()
        month_toolbar.title = dateFormatForMonth.format(compactcalendar_view.firstDayOfCurrentMonth)
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

    private fun prepareCalendar() {
        compactcalendar_view.run {
            setFirstDayOfWeek(Calendar.MONDAY)
            setCurrentDate(Calendar.getInstance().time)
            shouldScrollMonth(true)
            setUseThreeLetterAbbreviation(true)
        }

        compactcalendar_view.setListener(this)
    }

    private fun addEventToCalendar(eventDate: Date) {
        val compactEvent = com.github.sundeepk.compactcalendarview.domain.Event(Color.WHITE, eventDate.time)
        compactcalendar_view.addEvent(compactEvent)
    }

    private fun getConfirmedAssistanceEvents() {
        db.collection("Eventos").whereGreaterThanOrEqualTo("eventDate", Calendar.getInstance().time).orderBy("eventDate").get().addOnCompleteListener { task ->
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
                            addEventToCalendar(eventDate!!)
                        }
                    }

                }
            }
        }
    }

    private fun getEventsWithSpecifiedDate(eventDate: Date) {
        val eventsWithSpecifiedDate: ArrayList<Event> = ArrayList()

        db.collection("Eventos").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result) {
                    val geoPoint: GeoPoint? = document.getGeoPoint("latLng")
                    val date: Date? = document.getDate("eventDate")
                    var latLng: LatLng? = null
                    var dateToString: String? = null
                    val spanishLocale = Locale("es", "ES")
                    val simpleDateFormat = SimpleDateFormat("dd MMMM yyy HH:mm", spanishLocale)
                    simpleDateFormat.timeZone = TimeZone.getTimeZone("Europe/Madrid")

                    val tags: String = document.get("tags").toString()

                    geoPoint?.let {
                        latLng = LatLng(it.latitude, it.longitude)
                    }

                    date?.let {
                        dateToString = simpleDateFormat.format(it)
                    }

                    val event = Event(document.getString("eventId")!!, document.getString("category")!!, latLng!!, document.getString("name")!!, document.getString("description")!!, document.getString("placeId")!!, dateToString!!, tags.split(","))

                    for (assistingEventId in confirmedAssistanceEventsId) {
                        if (assistingEventId == event.eventId) {
                            val dateFormatGmt = SimpleDateFormat.getInstance()
                            dateFormatGmt.timeZone = TimeZone.getTimeZone("GMT")
                            val gmtDate = dateFormatGmt.format(date)
                            val gmtEventDate = dateFormatGmt.format(eventDate)

                            if (gmtDate.substringBefore(" ") == gmtEventDate.substringBefore(" ")) {
                                eventsWithSpecifiedDate.add(event)
                            }
                        }
                    }
                }

                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                schedule_recycler.layoutManager = layoutManager

                val adapter = CompleteEventsAdapter(eventsWithSpecifiedDate)
                schedule_recycler.adapter = adapter

                schedule_recycler.addItemDecoration(RecyclerItemDivider(this))
                adapter.notifyItemInserted(0)
            }
        }
    }

    private fun retrieveInfoAboutCurrentUser() {
        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.toObject(User::class.java)
                confirmedAssistanceEventsId = user!!.eventsAssisting

                onRetrieveFirebaseDataListener.onRetrieved()
            }
        }
    }
}
