package com.evesoftworks.javier_t.eventually.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.evesoftworks.javier_t.eventually.R
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.edit_profile_toolbar.*

class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        setSupportActionBar(editToolbar)
        editToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        editToolbar.setNavigationOnClickListener { finish() }

        getUserInfo()
    }

    private fun getUserInfo() {
        val retrievedUserData = intent.extras
        val arrayOfUserData = retrievedUserData.getStringArrayList("USERDATA")

        Picasso.get().load(FirebaseAuth.getInstance().currentUser!!.photoUrl).into(profile_my_pic)
        profile_my_name.setText(arrayOfUserData[0], TextView.BufferType.EDITABLE)
        profile_my_email.setText(arrayOfUserData[1], TextView.BufferType.EDITABLE)
        profile_my_username.setText(arrayOfUserData[2], TextView.BufferType.EDITABLE)
    }

    private fun userIsEditing() {

    }
}
