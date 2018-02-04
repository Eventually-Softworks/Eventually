package com.evesoftworks.javier_t.eventually.activities

import android.net.Uri
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem


import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.SectionsPagerAdapter
import com.evesoftworks.javier_t.eventually.fragments.ContactsFragment
import com.evesoftworks.javier_t.eventually.fragments.EventsFragment
import com.evesoftworks.javier_t.eventually.fragments.GroupsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.header_drawer.*
import kotlinx.android.synthetic.main.tabs_layout.*

class MainPageActivity : AppCompatActivity(), ContactsFragment.OnFragmentInteractionListener, EventsFragment.OnFragmentInteractionListener, GroupsFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO()
    }

    override fun onFragmentInteraction(uri: Uri) {}

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private lateinit var mToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter

        mToggle = ActionBarDrawerToggle(this, main_content, R.string.sidebaropen, R.string.sidebarclosed)
        main_content.addDrawerListener(mToggle)
        mToggle.syncState()

        setDataWithCurrentUser()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navigation_drawer.setNavigationItemSelectedListener(this)

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mToggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setDataWithCurrentUser() {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        db.collection("PreferenciasUsuario").document(currentUser?.uid as String).get().addOnSuccessListener {
            documentSnapshot ->
                nav_email.text = currentUser.email.toString()
        }
    }
}
