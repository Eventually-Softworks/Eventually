package com.evesoftworks.javier_t.eventually.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.GroupParticipantsAdapter
import com.evesoftworks.javier_t.eventually.constants.CustomResultCode
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.evesoftworks.javier_t.eventually.dbmodel.Group
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataListener
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataWithArgsListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_group_details.*
import kotlinx.android.synthetic.main.group_details_toolbar.*
import java.util.*
import kotlin.collections.ArrayList

class GroupDetailsActivity : AppCompatActivity(), OnRetrieveFirebaseDataListener, OnRetrieveFirebaseDataWithArgsListener {
    lateinit var currentlySeeingGroup: Group
    val onRetrieveFirebaseDataWithArgsListener = this
    lateinit var participants: ArrayList<String>
    var participantsArray = ArrayList<User>()
    val db = FirebaseFirestore.getInstance()
    val onRetrieveFirebaseDataListener = this

    override fun onRetrieved() {
        val adapter = GroupParticipantsAdapter(participantsArray)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        group_participants_recycle.layoutManager = layoutManager
        group_participants_recycle.adapter = adapter
    }

    override fun onRetrieve(args: String?, arrayList: ArrayList<Event>?, arrayId: ArrayList<String>?, userId: String?) {
        arrayId?.let {
            val mapGroups = HashMap<String, Any>()
            mapGroups["groups"] = it
            db.collection("Usuarios").document(userId!!).update(mapGroups).addOnCompleteListener {
                leave_group_button.doneLoadingAnimation(ContextCompat.getColor(this, R.color.colorPrimary), BitmapFactory.decodeResource(resources, R.drawable.ic_check_white_24dp))
                val returnIntent = Intent()
                setResult(CustomResultCode.GROUP_LEAVED, returnIntent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)

        setSupportActionBar(group_details_toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        prepareGroupDetails()

        if (leave_group_button.visibility == View.VISIBLE) {
            leave_group_button.setOnClickListener {
                leave_group_button.startAnimation()
                leaveGroupAction()
            }
        }

        if (delete_group_button.visibility == View.VISIBLE) {
            delete_group_button.setOnClickListener {
                delete_group_button.startAnimation()
                deleteGroupAction()
            }
        }
    }

    private fun deleteGroupAction() {
        db.collection("Grupos").document(currentlySeeingGroup.groupId).delete().addOnCompleteListener {
            if (it.isSuccessful) {
                delete_group_button.doneLoadingAnimation(ContextCompat.getColor(this, R.color.colorPrimary), BitmapFactory.decodeResource(resources, R.drawable.ic_check_white_24dp))
                val returnIntent = Intent()
                setResult(CustomResultCode.GROUP_DELETED, returnIntent)
                finish()
            } else {
                delete_group_button.revertAnimation {
                    delete_group_button.background = getDrawable(R.drawable.rounded_button_cancel)
                    delete_group_button.text = getString(R.string.error_try_again)
                }
            }
        }
    }

    private fun leaveGroupAction() {
        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.toObject(User::class.java)
                val groupsToPush = user!!.groups
                groupsToPush.remove(currentlySeeingGroup.groupId)

                onRetrieveFirebaseDataWithArgsListener.onRetrieve(arrayId = groupsToPush, userId = FirebaseAuth.getInstance().currentUser!!.uid)
            } else {
                leave_group_button.revertAnimation {
                    leave_group_button.background = getDrawable(R.drawable.rounded_button_cancel)
                    leave_group_button.text = getString(R.string.error_try_again)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return true
    }

    private fun prepareGroupDetails() {
        currentlySeeingGroup = intent.extras.getParcelable<Group>("aGroup")

        participants = intent.extras.getStringArrayList("participantsGroup")
        val storageReference = FirebaseStorage.getInstance().reference.child("groupsphotos/${currentlySeeingGroup.groupPhotoId}")

        storageReference.downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                Picasso.get().load(it.result).into(group_pic)
            }
        }

        group_details_toolbar.title = currentlySeeingGroup.groupName
        group_name.text = currentlySeeingGroup.groupName

        if (currentlySeeingGroup.adminUid == FirebaseAuth.getInstance().currentUser!!.uid) {
            leave_group_button.visibility = View.GONE
        } else {
            delete_group_button.visibility = View.GONE
        }

        retrieveUsersFromParticipantsId()
    }

    private fun retrieveUsersFromParticipantsId() {
        db.collection("Usuarios").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result) {
                    val user = document.toObject(User::class.java)

                    if (participants.contains(user.photoId)) {
                        participantsArray.add(user)
                    }
                }

                onRetrieveFirebaseDataListener.onRetrieved()
            }
        }
    }
}