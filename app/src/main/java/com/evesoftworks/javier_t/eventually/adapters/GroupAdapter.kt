package com.evesoftworks.javier_t.eventually.adapters

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.dbmodel.Group
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.single_group.view.*

class GroupAdapter(val groups: ArrayList<Group>) : RecyclerView.Adapter<GroupAdapter.Companion.GroupViewHolder>() {
    companion object {
        class GroupViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_event, parent, false)
        return GroupViewHolder(v as CardView)
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val storageReference = FirebaseStorage.getInstance().reference.child("groupsphotos/${groups[position].groupId}")

        storageReference.downloadUrl.addOnSuccessListener { Picasso.get().load(it).into(holder.cardView.group_image) }
        holder.cardView.group_name.text = groups[position].groupName

        holder.cardView.setOnClickListener {}
    }
}