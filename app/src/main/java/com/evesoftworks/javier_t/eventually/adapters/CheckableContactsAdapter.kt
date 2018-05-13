package com.evesoftworks.javier_t.eventually.adapters

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.activities.UserProfileActivity
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.RecyclerViewItemEnabler
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.checkable_single_contact.view.*
import kotlinx.android.synthetic.main.single_contact.view.*

class CheckableContactsAdapter(val users: ArrayList<User>) : RecyclerView.Adapter<CheckableContactsAdapter.Companion.ContactViewHolder>(), RecyclerViewItemEnabler {
    var mAllEnabled = false
    var selectedUsers = ArrayList<User>()

    companion object {
        class ContactViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.checkable_single_contact, parent, false)
        return ContactViewHolder(v as CardView)
    }

    override fun getItemEnabled(position: Int): Boolean {
        return true
    }

    override fun areAllItemsEnabled(): Boolean {
        return mAllEnabled
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val storageReference = FirebaseStorage.getInstance().reference.child("usersprofilepics/${users[position].photoId}")

        storageReference.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(holder.cardView.checkable_image)
        }

        holder.cardView.isEnabled = areAllItemsEnabled()
        holder.cardView.checkable_display_name.text = users[position].displayName
        holder.cardView.checkable_username.text = users[position].username
        holder.cardView.checkbox_user.isChecked = users[position].isSelected
        holder.cardView.checkbox_user.tag = users[position]

        holder.cardView.checkbox_user.setOnClickListener {
            val checkbox = it as CheckBox
            val user = checkbox.tag as User

            user.isSelected = checkbox.isChecked
            users[position].isSelected = checkbox.isChecked

            if (users[position].isSelected) {
                selectedUsers.add(users[position])
            } else {
                selectedUsers.remove(users[position])
            }
        }
    }
}