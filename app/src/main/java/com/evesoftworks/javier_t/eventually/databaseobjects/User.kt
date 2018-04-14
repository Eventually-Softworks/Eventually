package com.evesoftworks.javier_t.eventually.databaseobjects

import android.os.Parcel
import android.os.Parcelable
import java.util.*
import kotlin.collections.ArrayList

class User (): Parcelable {
    var categories: ArrayList<Category> = ArrayList()
    var username: String = ""
    var firstName: String = ""
    var eventsLiked: ArrayList<Event> = ArrayList()
    var groups: ArrayList<Group> = ArrayList()
    var friends: ArrayList<User> = ArrayList()

    constructor(parcel: Parcel) : this() {
        username = parcel.readString()
        firstName = parcel.readString()
    }

    constructor(categories: ArrayList<Category>, eventsLiked: ArrayList<Event>, firstName: String, friends: ArrayList<User>, groups: ArrayList<Group>, username: String): this() {
        this.categories = categories
        this.eventsLiked = eventsLiked
        this.firstName = firstName
        this.friends = friends
        this.groups = groups
        this.username = username
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(firstName)
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