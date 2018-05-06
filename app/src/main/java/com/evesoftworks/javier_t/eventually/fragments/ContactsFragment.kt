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
import com.evesoftworks.javier_t.eventually.utils.ContactsItemDivider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : Fragment() {
    var currentUserPreferences: ArrayList<String> = ArrayList()
    lateinit var contacts: ArrayList<User>
    lateinit var adapter: ContactsAdapter
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retrieveCurrentUserPreferences()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        contacts = ArrayList()
        adapter = ContactsAdapter(contacts)

        swipe_refresh_contacts.setOnRefreshListener {
            refreshContent()
        }
        swipe_refresh_contacts.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    private fun refreshContent() {
        prepareUsers()
        adapter.notifyDataSetChanged()
    }

    private fun prepareUsers() {
        contacts.clear()
        var coincidences = 0

        db.collection("Usuarios").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    if (document.id != FirebaseAuth.getInstance().currentUser!!.uid) {
                        val user: User = document.toObject(User::class.java)

                        for (i in 0 until currentUserPreferences.size) {
                            for (j in 0 until user.categories.size) {
                                if (user.categories[j].contains(currentUserPreferences[i])) {
                                    coincidences++
                                }
                            }
                        }

                        if (coincidences > 0) {
                            contacts.add(user)
                        }
                    }
                }

                contacts_recycler.setHasFixedSize(true)

                val layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
                contacts_recycler.layoutManager = layoutManager

                contacts_recycler.adapter = adapter
                contacts_recycler.addItemDecoration(ContactsItemDivider(activity!!.applicationContext))

                swipe_refresh_contacts.isRefreshing = false
            }
        }
    }

    private fun retrieveCurrentUserPreferences() {
        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.toObject(User::class.java)
                currentUserPreferences = user!!.categories

                prepareUsers()
            }
        }
    }
}
