package com.evesoftworks.javier_t.eventually.adapters

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.activities.UserProfileActivity
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.RecyclerViewItemEnabler
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.single_contact.view.*

class ContactsAdapter(val suggestions: ArrayList<User>) : RecyclerView.Adapter<ContactsAdapter.Companion.ContactViewHolder>(), RecyclerViewItemEnabler {
    var mAllEnabled = false

    companion object {
        class ContactViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_contact, parent, false)
        return ContactViewHolder(v as CardView)
    }

    override fun getItemEnabled(position: Int): Boolean {
        return true
    }

    override fun areAllItemsEnabled(): Boolean {
        return mAllEnabled
    }

    override fun getItemCount(): Int {
        return suggestions.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val storageReference = FirebaseStorage.getInstance().reference.child("usersprofilepics/${suggestions[position].photoId}")

        storageReference.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(holder.cardView.contact_image)
        }

        holder.cardView.isEnabled = areAllItemsEnabled()
        holder.cardView.contact_display_name.text = suggestions[position].displayName
        holder.cardView.contact_username.text = suggestions[position].username

        if (suggestions[position].isMatched) {
            holder.cardView.match_emoji.visibility = View.VISIBLE
        } else {
            holder.cardView.match_emoji.visibility = View.GONE
        }

        holder.cardView.setOnClickListener({
            val intent = Intent(holder.cardView.context, UserProfileActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("aContact", suggestions[position])
            intent.putExtras(bundle)

            holder.cardView.context.startActivity(intent)
        })
    }

    fun setAllItemsEnabled(enable: Boolean) {
        mAllEnabled = enable
        notifyItemRangeChanged(0, itemCount)
    }
}