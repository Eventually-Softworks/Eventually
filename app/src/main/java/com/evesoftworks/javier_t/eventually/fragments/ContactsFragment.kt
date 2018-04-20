package com.evesoftworks.javier_t.eventually.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.ContactsAdapter
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : Fragment() {
    lateinit var user: User
    val db = FirebaseFirestore.getInstance()
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val contacts = ArrayList<User>()

        db.collection("Usuarios").get().addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    val user: User = document.toObject(User::class.java)
                    contacts.add(user)
                }

                contacts_recycler.setHasFixedSize(true)

                val layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
                contacts_recycler.layoutManager = layoutManager

                val adapter = ContactsAdapter(contacts)

                contacts_recycler.adapter = adapter

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }
}
