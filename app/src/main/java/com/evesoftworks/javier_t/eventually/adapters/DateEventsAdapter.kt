package com.evesoftworks.javier_t.eventually.adapters

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.activities.AnEventActivity
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.single_event.view.*

class DateEventsAdapter(val events: ArrayList<Event>) : RecyclerView.Adapter<DateEventsAdapter.Companion.DateEventViewHolder>() {
    companion object {
        class DateEventViewHolder(val constraint: ConstraintLayout): RecyclerView.ViewHolder(constraint)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateEventViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_event, parent, false)
        return DateEventViewHolder(v as ConstraintLayout)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holderDate: DateEventViewHolder, position: Int) {
        val storageReference = FirebaseStorage.getInstance().reference.child("eventsphotos/${events[position].name}.jpg")

        storageReference.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(holderDate.constraint.singleevent_image)
        }
        holderDate.constraint.singleevent_name.text = events[position].name
        holderDate.constraint.singleevent_category.text = events[position].eventDate

        holderDate.constraint.setOnClickListener({
            val intent = Intent(holderDate.constraint.context, AnEventActivity::class.java)
            val bundle = Bundle()

            bundle.putParcelable("anEvent", events[position])
            intent.putExtras(bundle)
            holderDate.constraint.context.startActivity(intent)
        })
    }
}