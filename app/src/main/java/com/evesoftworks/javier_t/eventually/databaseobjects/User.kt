package com.evesoftworks.javier_t.eventually.databaseobjects

class User (val categories: ArrayList<Category>) {
    var groups: ArrayList<Group> = ArrayList<Group>()
    var friends: ArrayList<User> = ArrayList<User>()
}