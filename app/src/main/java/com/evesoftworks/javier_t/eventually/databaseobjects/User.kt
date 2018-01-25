package com.evesoftworks.javier_t.eventually.databaseobjects

data class User (val email: String, val password: String, val userName: String, val firstName: String, val lastName: String, val categories: ArrayList<Category>,
                 val groups: ArrayList<Group> = ArrayList<Group>(), val friends: ArrayList<User> = ArrayList<User>())