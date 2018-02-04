package com.evesoftworks.javier_t.eventually.databaseobjects

import java.sql.Timestamp

class Event() {
    lateinit var category: String
    lateinit var name: String

    constructor(category: String, name: String): this(){
        this.category = category
        this.name = name
    }
}