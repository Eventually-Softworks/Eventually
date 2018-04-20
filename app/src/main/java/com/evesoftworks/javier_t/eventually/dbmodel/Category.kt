package com.evesoftworks.javier_t.eventually.dbmodel

class Category() {
    lateinit var  preferenceName: String

    constructor(preferenceName: String): this() {
        this.preferenceName = preferenceName
    }
}