package com.evesoftworks.javier_t.eventually.adapters

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.activities.UserProfileActivity
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.single_participant.view.*

class GroupParticipantsAdapter(val participants: ArrayList<User>) : RecyclerView.Adapter<GroupParticipantsAdapter.Companion.GroupParticipantViewHolder>() {
    companion object {
        class GroupParticipantViewHolder(val cardView: ConstraintLayout) : RecyclerView.ViewHolder(cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupParticipantViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_participant, parent, false)
        return GroupParticipantViewHolder(v as ConstraintLayout)
    }

    override fun getItemCount(): Int {
        return participants.size
    }

    override fun onBindViewHolder(holder: GroupParticipantViewHolder, position: Int) {
        val storageReference = FirebaseStorage.getInstance().reference.child("usersprofilepics/${participants[position].photoId}")

        storageReference.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(holder.cardView.participant_image)
        }

        if (participants[position].photoId == FirebaseAuth.getInstance().currentUser!!.uid) {
            holder.cardView.participant_name.text = "Tú"
        } else {
            holder.cardView.participant_name.text = participants[position].displayName
        }

        holder.cardView.participant_username.text = participants[position].username

        holder.cardView.setOnClickListener({
            val intent = Intent(holder.cardView.context, UserProfileActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("aContact", participants[position])
            intent.putExtras(bundle)

            holder.cardView.context.startActivity(intent)
        })
    }
}