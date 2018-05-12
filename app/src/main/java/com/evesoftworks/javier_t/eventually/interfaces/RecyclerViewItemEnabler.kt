package com.evesoftworks.javier_t.eventually.interfaces

interface RecyclerViewItemEnabler {
    fun areAllItemsEnabled(): Boolean
    fun getItemEnabled(position: Int): Boolean
}