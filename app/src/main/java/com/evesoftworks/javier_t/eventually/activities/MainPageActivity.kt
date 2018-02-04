package com.evesoftworks.javier_t.eventually.activities

import android.net.Uri
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem


import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.SectionsPagerAdapter
import com.evesoftworks.javier_t.eventually.fragments.ContactsFragment
import com.evesoftworks.javier_t.eventually.fragments.EventsFragment
import com.evesoftworks.javier_t.eventually.fragments.GroupsFragment
import kotlinx.android.synthetic.main.activity_main_page.*

class MainPageActivity : AppCompatActivity(), ContactsFragment.OnFragmentInteractionListener, EventsFragment.OnFragmentInteractionListener, GroupsFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {}

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_page, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
