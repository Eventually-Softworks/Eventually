package com.evesoftworks.javier_t.eventually.adapters

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.activities.AnEventActivity
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.evesoftworks.javier_t.eventually.interfaces.RecyclerViewItemEnabler
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.single_complete_event.view.*

class CompleteEventsAdapter(val events: ArrayList<Event>) : RecyclerView.Adapter<CompleteEventsAdapter.Companion.CompleteEventViewHolder>(), RecyclerViewItemEnabler {
    var mAllEnabled = false

    companion object {
        class CompleteEventViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)
    }

    override fun getItemEnabled(position: Int): Boolean {
        return true
    }

    override fun areAllItemsEnabled(): Boolean {
        return mAllEnabled
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompleteEventViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_complete_event, parent, false)
        return CompleteEventViewHolder(v as CardView)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holderComplete: CompleteEventViewHolder, position: Int) {
        val storageReference = FirebaseStorage.getInstance().reference.child("eventsphotos/${events[position].eventId}.jpg")

        storageReference.downloadUrl.addOnSuccessListener { Picasso.get().load(it).into(holderComplete.cardView.event_complete_image) }
        holderComplete.cardView.event_complete_title.text = events[position].name
        holderComplete.cardView.event_complete_category.text = events[position].category
        holderComplete.cardView.event_complete_date.text = events[position].eventDate

        holderComplete.cardView.setOnClickListener {
            val intent = Intent(holderComplete.cardView.context, AnEventActivity::class.java)
            val bundle = Bundle()

            bundle.putParcelable("anEvent", events[position])
            intent.putExtras(bundle)
            holderComplete.cardView.context.startActivity(intent)
        }
    }

    fun setAllItemsEnabled(enable: Boolean) {
        mAllEnabled = enable
        notifyItemRangeChanged(0, itemCount)
    }
}