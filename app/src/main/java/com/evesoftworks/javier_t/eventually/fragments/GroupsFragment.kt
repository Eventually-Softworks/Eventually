package com.evesoftworks.javier_t.eventually.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.GroupAdapter
import com.evesoftworks.javier_t.eventually.dbmodel.Group
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_groups.*

class GroupsFragment : Fragment(), OnRetrieveFirebaseDataListener {
    val db = FirebaseFirestore.getInstance()
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var currentGroupsId: ArrayList<String> = ArrayList()
    var currentGroups: ArrayList<Group> = ArrayList()
    val onRetrieveFirebaseDataListener = this

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retrieveCurrentGroupsId()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        swipeRefreshLayout = activity!!.findViewById(R.id.swipe_refresh_groups)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener { refreshContent() }

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
