package com.evesoftworks.javier_t.eventually.databaseobjects

class Category() {
    lateinit var  preferenceName: String

    constructor(preferenceName: String): this() {
        this.preferenceName = preferenceName
    }
}