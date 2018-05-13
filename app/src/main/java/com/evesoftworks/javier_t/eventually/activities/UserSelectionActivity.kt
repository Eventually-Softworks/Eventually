package com.evesoftworks.javier_t.eventually.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.CheckableContactsAdapter
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataListener
import com.evesoftworks.javier_t.eventually.utils.RecyclerItemDivider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_selection.*
import kotlinx.android.synthetic.main.fragment_contacts.*

class UserSelectionActivity : AppCompatActivity() {
    /*val adapter = CheckableContactsAdapter(coincidences)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        checkable_user_recycler.layoutManager = layoutManager

        checkable_user_recycler.adapter = adapter
        checkable_user_recycler.addItemDecoration(RecyclerItemDivider(this))*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_selection)
    }
}
