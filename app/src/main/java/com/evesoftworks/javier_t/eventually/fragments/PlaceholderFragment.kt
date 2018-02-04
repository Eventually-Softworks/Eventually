package com.evesoftworks.javier_t.eventually.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.evesoftworks.javier_t.eventually.R
import kotlinx.android.synthetic.main.fragment_main_page.view.*

class PlaceholderFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main_page, container, false)
        rootView.section_label.text = getString(R.string.section_format, arguments.getInt(ARG_SECTION_NUMBER))
        return rootView
    }

    companion object {
        private val ARG_SECTION_NUMBER = "section_number"

        fun newInstance(sectionNumber: Int): Fragment? {
            var fragment: Fragment? = null

            when (sectionNumber) {
                1 -> fragment = EventsFragment()
                2 -> fragment = ContactsFragment()
                3 -> fragment = GroupsFragment()
            }

            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)

            return fragment
        }
    }
}
