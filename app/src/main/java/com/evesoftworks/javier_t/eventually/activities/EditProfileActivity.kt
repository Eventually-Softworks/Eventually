package com.evesoftworks.javier_t.eventually.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.databaseobjects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.edit_profile_toolbar.*

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(view: View?) {
        if (view?.id == fab_ok_edit.id) {
            updateInfo()
        }
    }

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        getUserInfo()
        setSupportActionBar(editToolbar)
        editToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        editToolbar.setNavigationOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View?) {
                finish()
            }
        })

        fab_ok_edit.setOnClickListener(this)

    }

    private fun getUserInfo() {
        db.collection("PreferenciasUsuario").document(FirebaseAuth.getInstance().currentUser?.uid as String).get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject<User>(User::class.java)

            edit_firstname.setText(user.firstName, TextView.BufferType.EDITABLE)
            edit_lastname.setText(user.lastName, TextView.BufferType.EDITABLE)
            edit_username.setText(user.username, TextView.BufferType.EDITABLE)
        }
    }

    private fun updateInfo() {
        val firstNameEdited: String = edit_firstname.text.toString()
        val lastNameEdited: String = edit_lastname.text.toString()
        val userNameEdited: String = edit_username.text.toString()

        val updates: HashMap<String, String> = HashMap<String, String>()

        updates.put("firstName", firstNameEdited)
        updates.put("lastName", lastNameEdited)
        updates.put("username", userNameEdited)

        db.collection("PreferenciasUsuario").document(FirebaseAuth.getInstance().currentUser?.uid as String).update(updates as Map<String, String>)
    }
}
