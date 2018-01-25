package com.evesoftworks.javier_t.eventually.databaseobjects

import java.util.*

data class Event(val eventId: String, val eventName: String, val eventDescription: String, val location: ArrayList<String>, val eventDate: Date)