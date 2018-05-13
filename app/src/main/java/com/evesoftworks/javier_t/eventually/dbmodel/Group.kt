package com.evesoftworks.javier_t.eventually.dbmodel

import java.util.*

class Group() {
    lateinit var groupId: String
    lateinit var groupName: String
    lateinit var participants: ArrayList<String>
    lateinit var adminUid: String

    constructor(groupId: String, groupName: String, participants: ArrayList<String>, adminUid: String) : this() {
        this.groupId = groupId
        this.groupName = groupName
        this.participants = participants
        this.adminUid = adminUid
    }
}