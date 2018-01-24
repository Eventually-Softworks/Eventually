package com.evesoftworks.javier_t.eventually.databaseobjects

class User () {
    lateinit var email: String
    lateinit var password: String

    constructor(email: String, password: String): this() {
        this.email = email
        this.password = password
    }
}