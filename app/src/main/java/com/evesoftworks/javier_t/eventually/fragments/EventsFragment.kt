package com.evesoftworks.javier_t.eventually.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.EventsAdapter
import com.evesoftworks.javier_t.eventually.databaseobjects.Event
import com.evesoftworks.javier_t.eventually.databaseobjects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_events.*

class EventsFragment : Fragment() {
    lateinit var events: ArrayList<Event>
    lateinit var user: User
    val db = FirebaseFirestore.getInstance()
    val mAuth = FirebaseAuth.getInstance()

    companion object {
        fun newInstance(): EventsFragment {
            return EventsFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getEventInfo()
        return inflater!!.inflate(R.layout.fragment_events, container, false)
    }

    private fun getEventInfo() {
        events = ArrayList<Event>()

        db.collection("Eventos").get().addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    val event: Event = document.toObject(Event::class.java)
                    events.add(event)
                }

                val eventsAdapter = EventsAdapter(events, context)
                listView.adapter = eventsAdapter
            }
        }
    }
}
