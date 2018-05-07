package com.evesoftworks.javier_t.eventually.dbmodel

import android.os.Parcel
import android.os.Parcelable
import kotlin.collections.ArrayList

class User (): Parcelable {
    var categories: ArrayList<String> = ArrayList()
    var username: String = ""
    var eventsLiked: ArrayList<String> = ArrayList()
    var eventsAssisting: ArrayList<String> = ArrayList()
    var groups: ArrayList<String> = ArrayList()
    var friends: ArrayList<String> = ArrayList()
    var photoId: String = ""

    constructor(parcel: Parcel) : this() {
        username = parcel.readString()
    }

    constructor(categories: ArrayList<String>, eventsLiked: ArrayList<String>, eventsAssisting: ArrayList<String>, username: String, friends: ArrayList<String>, groups: ArrayList<String>, photoId: String): this() {
        this.categories = categories
        this.eventsLiked = eventsLiked
        this.eventsAssisting = eventsAssisting
        this.username = username
        this.friends = friends
        this.groups = groups
        this.photoId = photoId
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(photoId)
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