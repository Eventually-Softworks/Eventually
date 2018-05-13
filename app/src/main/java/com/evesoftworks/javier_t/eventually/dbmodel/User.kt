package com.evesoftworks.javier_t.eventually.dbmodel

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlin.collections.ArrayList

class User (): Parcelable {
    var categories: ArrayList<String> = ArrayList()
    var username: String = ""
    var displayName: String = ""
    var eventsLiked: ArrayList<String> = ArrayList()
    var eventsAssisting: ArrayList<String> = ArrayList()
    var groups: ArrayList<String> = ArrayList()
    var friends: ArrayList<String> = ArrayList()
    var photoId: String = ""
    @Exclude
    var isMatched: Boolean = false
    @Exclude
    var isSelected: Boolean = false

    constructor(parcel: Parcel) : this() {
        username = parcel.readString()
        displayName = parcel.readString()
        photoId = parcel.readString()
        isMatched = parcel.readByte() != 0.toByte()
    }

    constructor(categories: ArrayList<String>, displayName: String, eventsLiked: ArrayList<String>, eventsAssisting: ArrayList<String>, username: String, friends: ArrayList<String>, groups: ArrayList<String>, photoId: String): this() {
        this.displayName = displayName
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
        parcel.writeString(displayName)
        parcel.writeString(photoId)
        parcel.writeByte(if (isMatched) 1 else 0)
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