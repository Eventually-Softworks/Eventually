package com.evesoftworks.javier_t.eventually.adapters

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.activities.AnEventActivity
import com.evesoftworks.javier_t.eventually.databaseobjects.Event
import kotlinx.android.synthetic.main.single_event.view.*


class EventsAdapter(val events: ArrayList<Event>): RecyclerView.Adapter<EventsAdapter.Companion.EventViewHolder>() {
     companion object {
         class EventViewHolder(val constraint: ConstraintLayout): RecyclerView.ViewHolder(constraint)
     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_event, parent, false)
        return EventViewHolder(v as ConstraintLayout)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.constraint.singleevent_name.text = events[position].name
        holder.constraint.singleevent_category.text = events[position].category
        //definir imagen

        holder.constraint.setOnClickListener({
            val intent = Intent(holder.constraint.context, AnEventActivity::class.java)
            val bundle = Bundle()

            bundle.putParcelable("anEvent", events[position])
            intent.putExtras(bundle)
            holder.constraint.context.startActivity(intent)
        })
     }
}