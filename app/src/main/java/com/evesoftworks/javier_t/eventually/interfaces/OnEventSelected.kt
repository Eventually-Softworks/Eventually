package com.evesoftworks.javier_t.eventually.interfaces

import com.evesoftworks.javier_t.eventually.databaseobjects.Event

interface OnEventSelected {
    fun myEventSelectedListener(event: Event)
}