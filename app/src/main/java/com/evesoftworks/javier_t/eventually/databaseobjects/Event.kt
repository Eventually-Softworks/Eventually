package com.evesoftworks.javier_t.eventually.databaseobjects

import java.util.*

class Event(val eventId: String, val eventName: String, val eventDescription: String, val location: ArrayList<String>, val eventDate: Date) {
    var likes: Int = 0
}