package com.evesoftworks.javier_t.eventually.databaseobjects

import java.util.*
import kotlin.collections.ArrayList

class User () {
    var categories: ArrayList<Category> = ArrayList<Category>()
    var username: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var eventsLiked: ArrayList<Event> = ArrayList<Event>()
    var groups: ArrayList<Group> = ArrayList<Group>()
    var friends: ArrayList<User> = ArrayList<User>()

    constructor(categories: ArrayList<Category>, eventsLiked: ArrayList<Event>, firstName: String, friends: ArrayList<User>, groups: ArrayList<Group>, lastName: String, username: String): this() {
        this.categories = categories
        this.eventsLiked = eventsLiked
        this.firstName = firstName
        this.lastName = lastName
        this.friends = friends
        this.groups = groups
        this.username = username
    }

    constructor(categories: ArrayList<Category>): this() {
        this.categories = categories
    }


}