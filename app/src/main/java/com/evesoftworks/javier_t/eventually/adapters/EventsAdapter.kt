package com.evesoftworks.javier_t.eventually.adapters

import android.content.Context
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.databaseobjects.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.single_event.view.*


class EventsAdapter(val events: ArrayList<Event>, val context: Context) : BaseAdapter() {
    private val references = FirebaseFirestore.getInstance().collection("Eventos")

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        var convertView = view

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.single_event, viewGroup, false)

            val currentEvent = getItem(position) as Event

            convertView.singleevent_name.text = currentEvent.name
            convertView.singleevent_category.text = currentEvent.category
        }

        return convertView!!
    }

    override fun getItem(position: Int): Any {
        return events[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return events.size
    }

}