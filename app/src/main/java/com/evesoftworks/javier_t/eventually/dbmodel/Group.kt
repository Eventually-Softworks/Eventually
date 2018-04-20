package com.evesoftworks.javier_t.eventually.dbmodel

import java.util.*

data class Group (val groupName: String, val participants: ArrayList<User>, val dateOfCreation: Date)