package com.evesoftworks.javier_t.eventually.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.CheckableContactsAdapter
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataListener
import com.evesoftworks.javier_t.eventually.utils.RecyclerItemDivider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_selection.*
import kotlinx.android.synthetic.main.fragment_contacts.*

class UserSelectionActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var selectableUsers: ArrayList<User>
    lateinit var groupId: String
    lateinit var adapter: CheckableContactsAdapter

    override fun onClick(v: View?) {
        val currentCheckedUsers = adapter.selectedUsers

        if (currentCheckedUsers.isEmpty()) {
            noSelectedUsersDialog()
        } else {
            Log.d("hola", currentCheckedUsers.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_selection)

        selectableUsers = intent.extras.getParcelableArrayList("selectableUsers")
        groupId = intent.extras.getString("prepareDocId")

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
}
