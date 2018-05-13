package com.evesoftworks.javier_t.eventually.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.CheckableContactsAdapter
import com.evesoftworks.javier_t.eventually.constants.CustomResultCode
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataWithArgsListener
import com.evesoftworks.javier_t.eventually.interfaces.OnSuccesfullyDocumentCreatedListener
import com.evesoftworks.javier_t.eventually.utils.RecyclerItemDivider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_selection.*
import kotlinx.android.synthetic.main.checkable_group_toolbar.*

class UserSelectionActivity : AppCompatActivity(), View.OnClickListener, OnSuccesfullyDocumentCreatedListener, OnRetrieveFirebaseDataWithArgsListener {
    lateinit var selectableUsers: ArrayList<User>
    lateinit var groupId: String
    lateinit var groupName: String
    lateinit var adapter: CheckableContactsAdapter
    val db = FirebaseFirestore.getInstance()
    val onSuccesfullyDocumentCreatedListener = this
    val onRetrieveFirebaseDataWithArgsListener = this
    val participantsId = ArrayList<String>()

    override fun onDocumentCreated(args: String?) {
        args?.let {
            for (userId in participantsId) {
                retrieveGroupsForEachSelectedUser(userId, groupId)
            }
        }
    }

    override fun onRetrieve(args: String?, arrayList: ArrayList<Event>?, arrayId: ArrayList<String>?, userId: String?) {
        arrayId?.let {
            val mapGroups = HashMap<String, Any>()
            mapGroups["groups"] = it
            db.collection("Usuarios").document(userId!!).update(mapGroups).addOnCompleteListener {
                val returnIntent = Intent()
                setResult(CustomResultCode.GROUP_CREATED, returnIntent)
                finish()
            }
        }
    }

    override fun onClick(v: View?) {
        val currentCheckedUsers = adapter.selectedUsers

        if (currentCheckedUsers.isEmpty()) {
            noSelectedUsersDialog()
        } else {
            createGroupInFirebase(currentCheckedUsers)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_selection)

        selectableUsers = intent.extras.getParcelableArrayList("selectableUsers")
        groupId = intent.extras.getString("prepareDocId")
        groupName = intent.extras.getString("groupName")

        setSupportActionBar(checkable_group_toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = groupName

        prepareCheckList()
        checkable_creation_group.setOnClickListener(this)
    }

    private fun noSelectedUsersDialog() {
        android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("No se puede crear el grupo")
                .setMessage("Debes seleccionar al menos un usuario para crear el grupo")
                .setPositiveButton(getString(R.string.logout_ok), null).show()
    }

    private fun prepareCheckList() {
        adapter = CheckableContactsAdapter(selectableUsers)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        checkable_user_recycler.layoutManager = layoutManager

        checkable_user_recycler.adapter = adapter
        checkable_user_recycler.addItemDecoration(RecyclerItemDivider(this))
    }


    private fun createGroupInFirebase(currentCheckedUsers: ArrayList<User>) {
        participantsId.add(FirebaseAuth.getInstance().currentUser!!.uid)

        for (currentChecked in currentCheckedUsers) {
            participantsId.add(currentChecked.photoId)
        }

        val data = HashMap<String, Any>()
        data["groupId"] = groupId
        data["groupName"] = groupName
        data["groupPhotoId"] = groupId
        data["participants"] = participantsId
        data["adminUid"] = FirebaseAuth.getInstance().currentUser!!.uid

        db.collection("Grupos").document(groupId).set(data).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccesfullyDocumentCreatedListener.onDocumentCreated(groupId)
            }
        }
    }

    private fun retrieveGroupsForEachSelectedUser(userId: String, createdGroupId: String) {
        db.collection("Usuarios").document(userId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.toObject(User::class.java)
                val groupsToPush = user!!.groups

                groupsToPush.add(createdGroupId)
                onRetrieveFirebaseDataWithArgsListener.onRetrieve(arrayId = groupsToPush, userId = user.photoId)
            }
        }
    }
}
