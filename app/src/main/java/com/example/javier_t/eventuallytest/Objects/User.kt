package com.example.javier_t.eventuallytest.Objects

class User () {
    lateinit var email: String
    lateinit var password: String

    constructor(email: String, password: String): this() {
        this.email = email
        this.password = password
    }
}