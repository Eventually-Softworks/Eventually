package com.evesoftworks.javier_t.eventually.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.evesoftworks.javier_t.eventually.fragments.PlaceholderFragment

class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return PlaceholderFragment.newInstance(position + 1)!!
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return "SECTION 1"
            1 -> return "SECTION 2"
            2 -> return "SECTION 3"
        }

        return ""
    }
}