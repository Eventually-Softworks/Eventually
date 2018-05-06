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

class EventsFragment : Fragment() {
    var events: ArrayList<Event> = ArrayList<Event>()
    var sections: ArrayList<EventSection> = ArrayList<EventSection>()
    lateinit var user: User
    val db = FirebaseFirestore.getInstance()
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.setHasFixedSize(true)

        setUpData()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    private fun getEventInfo() {
        events = ArrayList()

        db.collection("Eventos").get().addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    val geoPoint: GeoPoint? = document.getGeoPoint("latLng")
                    val eventDate: Date? = document.getDate("eventDate")
                    var latLng: LatLng? = null
                    var dateToString: String? = null

                    geoPoint?.let {
                        latLng = LatLng(it.latitude, it.longitude)
                    }

                    eventDate?.let {
                        val spanishLocale = Locale("es", "ES")
                        val simpleDateFormat = SimpleDateFormat("dd MMMM yyy HH:mm", spanishLocale)
                        simpleDateFormat.timeZone = TimeZone.getTimeZone("Europe/Madrid")
                        dateToString = simpleDateFormat.format(it)
                    }

                    val event = Event(document.getString("category")!!, latLng!!, document.getString("name")!!, document.getString("description")!!, document.getString("placeId")!!, dateToString!!)
                    events.add(event)
                }

                val layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
                recyclerView.layoutManager = layoutManager

                val adapter = EventSectionAdapter(sections)
                recyclerView.adapter = adapter

            }
        }
    }

    private fun createSections() {
        sections.add(EventSection("Te encantarán", events))
        sections.add(EventSection("Próximamente", events))
        sections.add(EventSection("Sugeridos para ti", events))
    }

    private fun setUpData() {
        getEventInfo()
        createSections()
    }
}
