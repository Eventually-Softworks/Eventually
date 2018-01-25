package com.evesoftworks.javier_t.eventually.databaseobjects

import java.util.*

data class Group (val groupName: String, val participants: ArrayList<User>, val dateOfCreation: Date)