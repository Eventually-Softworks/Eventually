package com.evesoftworks.javier_t.eventually.databaseobjects

import android.os.Parcel
import android.os.Parcelable
import java.util.*
import kotlin.collections.ArrayList

class User (): Parcelable {
    var categories: ArrayList<Category> = ArrayList<Category>()
    var username: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var eventsLiked: ArrayList<Event> = ArrayList<Event>()
    var groups: ArrayList<Group> = ArrayList<Group>()
    var friends: ArrayList<User> = ArrayList<User>()

    constructor(parcel: Parcel) : this() {
        username = parcel.readString()
        firstName = parcel.readString()
        lastName = parcel.readString()
    }

    constructor(categories: ArrayList<Category>, eventsLiked: ArrayList<Event>, firstName: String, friends: ArrayList<User>, groups: ArrayList<Group>, lastName: String, username: String): this() {
        this.categories = categories
        this.eventsLiked = eventsLiked
        this.firstName = firstName
        this.lastName = lastName
        this.friends = friends
        this.groups = groups
        this.username = username
    }

    constructor(categories: ArrayList<Category>): this() {
        this.categories = categories
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }


}