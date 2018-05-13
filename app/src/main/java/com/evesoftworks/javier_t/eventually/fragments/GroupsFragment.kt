package com.evesoftworks.javier_t.eventually.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.activities.OneGroupActivity
import com.evesoftworks.javier_t.eventually.adapters.GroupAdapter
import com.evesoftworks.javier_t.eventually.dbmodel.Group
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_groups.*

class GroupsFragment : Fragment(), OnRetrieveFirebaseDataListener, EventListener<QuerySnapshot> {
    val db = FirebaseFirestore.getInstance()
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var currentGroupsId: ArrayList<String> = ArrayList()
    var currentGroups: ArrayList<Group> = ArrayList()
    val onRetrieveFirebaseDataListener = this
    lateinit var docRef: Query
    lateinit var groupRef: Query

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retrieveCurrentGroupsId()
    }

    override fun onEvent(querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException?) {
        if (firebaseFirestoreException != null) {
            return
        }

        for (documentChanges in querySnapshot!!.documentChanges) {
            when (documentChanges.type) {
                DocumentChange.Type.ADDED -> {
                }
                DocumentChange.Type.MODIFIED -> {
                    refreshContent()
                }
                DocumentChange.Type.REMOVED -> {
                    refreshContent()
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        swipeRefreshLayout = activity!!.findViewById(R.id.swipe_refresh_groups)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener { refreshContent() }

        docRef = db.collection("Usuarios").whereEqualTo("photoId", FirebaseAuth.getInstance().currentUser!!.uid)
        groupRef = db.collection("Grupos").whereEqualTo("adminUid", FirebaseAuth.getInstance().currentUser!!.uid)

        docRef.addSnapshotListener(this)
        groupRef.addSnapshotListener(this)

        fab_create_group.setOnClickListener { goToOneGroupActivity() }

    }

    private fun goToOneGroupActivity() {
        val docId = db.collection("Grupos").document().id
        val intent = Intent(activity?.applicationContext, OneGroupActivity::class.java)
        intent.putExtra("collectionId", docId)
        startActivityForResult(intent, 11)
    }

    override fun onRetrieved() {
        retrieveCurrentGroups()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    private fun refreshContent() {
        retrieveCurrentGroupsId()
    }

    private fun retrieveCurrentGroupsId() {
        currentGroupsId.clear()

        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.toObject(User::class.java)
                currentGroupsId = user!!.groups

                onRetrieveFirebaseDataListener.onRetrieved()
            }
        }

    }

    private fun retrieveCurrentGroups() {
        currentGroups.clear()

        db.collection("Grupos").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result) {
                    val group = document.toObject(Group::class.java)

                    for (groupId in currentGroupsId) {
                        if (groupId == group.groupId) {
                            currentGroups.add(group)
                        }
                    }
                }

                groups_recycler.setHasFixedSize(true)

                val adapter = GroupAdapter(currentGroups)
                val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)
                staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

                groups_recycler.layoutManager = staggeredGridLayoutManager
                groups_recycler.adapter = adapter

                swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}
