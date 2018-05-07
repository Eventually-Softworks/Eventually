package com.evesoftworks.javier_t.eventually.dbmodel

import java.util.*

class Group() {
    lateinit var groupName: String
    lateinit var participants: ArrayList<User>
    lateinit var adminUid: String

    constructor(groupName: String, participants: ArrayList<User>, adminUid: String) : this() {
        this.groupName = groupName
        this.participants = participants
        this.adminUid = adminUid
    }
}