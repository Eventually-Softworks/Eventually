package com.evesoftworks.javier_t.eventually.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.EventSectionAdapter
import com.evesoftworks.javier_t.eventually.adapters.EventsAdapter
import com.evesoftworks.javier_t.eventually.databaseobjects.Event
import com.evesoftworks.javier_t.eventually.databaseobjects.EventSection
import com.evesoftworks.javier_t.eventually.databaseobjects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_events.*

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
        events = ArrayList<Event>()

        db.collection("Eventos").get().addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    val event: Event = document.toObject(Event::class.java)
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
