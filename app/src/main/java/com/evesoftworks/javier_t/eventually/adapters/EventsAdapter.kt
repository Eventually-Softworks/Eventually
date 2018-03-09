package com.evesoftworks.javier_t.eventually.adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.databaseobjects.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.single_event.view.*


class EventsAdapter(val events: ArrayList<Event>): RecyclerView.Adapter<EventsAdapter.Companion.EventViewHolder>() {
     companion object {
         class EventViewHolder(val constraint: ConstraintLayout): RecyclerView.ViewHolder(constraint)
     }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): EventViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.single_event, parent, false)
        return EventViewHolder(v as ConstraintLayout)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.constraint.singleevent_name.text = events[position].name
        holder.constraint.singleevent_category.text = events[position].category
        //definir imagen
     }
}