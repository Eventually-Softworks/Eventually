package com.evesoftworks.javier_t.eventually.databaseobjects

import java.util.*
import kotlin.collections.ArrayList

class User (var categories: ArrayList<Category>) {
    var eventsLiked: ArrayList<Event> = ArrayList<Event>()
    var groups: ArrayList<Group> = ArrayList<Group>()
    var friends: ArrayList<User> = ArrayList<User>()
    var username: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var birthDate: Date = Date()
}