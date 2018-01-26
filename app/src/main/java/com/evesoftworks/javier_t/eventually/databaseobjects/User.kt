package com.evesoftworks.javier_t.eventually.databaseobjects

class User (val categories: ArrayList<Category>) {
    lateinit var email: String
    lateinit var password: String
    lateinit var userName: String
    lateinit var firstName: String
    lateinit var lastName: String
    var groups: ArrayList<Group> = ArrayList<Group>()
    var friends: ArrayList<User> = ArrayList<User>()
}