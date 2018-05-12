package com.evesoftworks.javier_t.eventually.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.ContactsAdapter
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataListener
import com.evesoftworks.javier_t.eventually.utils.ContactsItemDivider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : Fragment(), OnRetrieveFirebaseDataListener, EventListener<QuerySnapshot> {
    var currentUserPreferences: ArrayList<String> = ArrayList()
    val onRetrieveFirebaseDataListener = this
    lateinit var docRef: Query
    var currentContacts: ArrayList<String> = ArrayList()
    lateinit var suggestions: ArrayList<User>
    lateinit var adapter: ContactsAdapter
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retrieveCurrentUserPreferences()
        docRef = db.collection("Usuarios").whereEqualTo("photoId", FirebaseAuth.getInstance().currentUser!!.uid)
        docRef.addSnapshotListener(this)
    }

    override fun onEvent(querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException?) {
        if (firebaseFirestoreException != null) {
            return
        }

        for (documentChanges in querySnapshot!!.documentChanges) {
            when (documentChanges.type) {
                DocumentChange.Type.ADDED -> {}
                DocumentChange.Type.MODIFIED -> {
                    retrieveCurrentUserPreferences()
                }
                DocumentChange.Type.REMOVED -> {}
            }
        }
    }

    override fun onRetrieved() {

        for (singleUser in suggestions) {
            for (possibleCoincidence in singleUser.friends) {
                if (possibleCoincidence == FirebaseAuth.getInstance().currentUser!!.uid) {
                    for (finalCoincidence in currentContacts) {
                        if (finalCoincidence == singleUser.photoId) {
                            singleUser.isMatched = true
                        }
                    }
                }
            }
        }

        contacts_recycler.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
        contacts_recycler.layoutManager = layoutManager

        contacts_recycler.adapter = adapter
        contacts_recycler.addItemDecoration(ContactsItemDivider(activity!!.applicationContext))

        swipeRefreshLayout.isRefreshing = false
        adapter.setAllItemsEnabled(!swipeRefreshLayout.isRefreshing)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        swipeRefreshLayout = activity!!.findViewById(R.id.swipe_refresh_contacts)

        suggestions = ArrayList()
        adapter = ContactsAdapter(suggestions)

        swipeRefreshLayout.setOnRefreshListener { refreshContent() }
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    private fun refreshContent() {
        adapter.setAllItemsEnabled(!swipeRefreshLayout.isRefreshing)
        retrieveCurrentUserPreferences()
    }

    private fun prepareUsers() {
        suggestions.clear()

        var coincidences = 0

        db.collection("Usuarios").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    if (document.id != FirebaseAuth.getInstance().currentUser!!.uid) {
                        val user: User = document.toObject(User::class.java)

                        for (i in 0 until currentUserPreferences.size) {
                            coincidences = 0

                            for (j in 0 until user.categories.size) {
                                if (currentUserPreferences.contains(user.categories[j])) {
                                    coincidences++
                                }
                            }
                        }

                        if (coincidences > 0) {
                            suggestions.add(user)
                        }
                    }
                }

                onRetrieveFirebaseDataListener.onRetrieved()
            }
        }
    }

    private fun retrieveCurrentUserPreferences() {
        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.toObject(User::class.java)
                currentUserPreferences = user!!.categories
                currentContacts = user.friends

                prepareUsers()
            }
        }
    }
}
