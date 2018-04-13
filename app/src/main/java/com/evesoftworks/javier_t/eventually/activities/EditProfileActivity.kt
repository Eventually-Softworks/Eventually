package com.evesoftworks.javier_t.eventually.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.databaseobjects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.edit_profile_toolbar.*

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        getUserInfo()
        setSupportActionBar(editToolbar)
        editToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        editToolbar.setNavigationOnClickListener { finish() }

    }

    private fun getUserInfo() {
        val retrievedUserData = intent.extras
        val arrayOfUserData = retrievedUserData.getStringArrayList("USERDATA")

        profile_my_name.setText(arrayOfUserData[0], TextView.BufferType.EDITABLE)
        profile_my_username.setText(arrayOfUserData[1], TextView.BufferType.EDITABLE)
        profile_my_email.setText(arrayOfUserData[2], TextView.BufferType.EDITABLE)
    }

    private fun userIsEditing() {

    }
}
