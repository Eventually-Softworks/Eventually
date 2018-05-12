package com.evesoftworks.javier_t.eventually.adapters

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.activities.CompleteEventsListActivity
import com.evesoftworks.javier_t.eventually.dbmodel.EventSection
import kotlinx.android.synthetic.main.single_event_section.view.*


class EventSectionAdapter(val sectionArray: ArrayList<EventSection>): RecyclerView.Adapter<EventSectionAdapter.Companion.ViewHolder>() {
    companion object {
        class ViewHolder(var cardView: CardView): RecyclerView.ViewHolder(cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_event_section, parent, false)
        return ViewHolder(v as CardView)
    }

    override fun getItemCount(): Int {
        return sectionArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardView.title.text = sectionArray[position].title
        holder.cardView.eventRecycle.adapter = EventsAdapter(sectionArray[position].events)
        holder.cardView.eventRecycle.setHasFixedSize(true)
        holder.cardView.eventRecycle.layoutManager = LinearLayoutManager(holder.cardView.context, LinearLayoutManager.HORIZONTAL, false)

        holder.cardView.show_more_button.setOnClickListener {
            val intent = Intent(holder.cardView.context, CompleteEventsListActivity::class.java)
            val bundle = Bundle()
            bundle.putString("aCompleteEvent", sectionArray[position].title)
            intent.putExtras(bundle)

            holder.cardView.context.startActivity(intent)
        }
    }
}