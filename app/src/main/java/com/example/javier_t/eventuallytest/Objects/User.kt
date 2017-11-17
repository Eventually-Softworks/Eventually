package com.example.javier_t.eventuallytest.Objects

/**
 * Created by Javier_T on 11/5/2017.
 */

class User () {
    lateinit var email: String
    lateinit var password: String

    constructor(email: String, password: String): this() {
        this.email = email
        this.password = password
    }
}