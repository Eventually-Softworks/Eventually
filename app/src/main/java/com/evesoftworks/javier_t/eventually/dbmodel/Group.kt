package com.evesoftworks.javier_t.eventually.dbmodel

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Group() : Parcelable {
    lateinit var groupId: String
    lateinit var groupName: String
    lateinit var groupPhotoId: String
    var participants: ArrayList<String> = ArrayList()
    lateinit var adminUid: String

    constructor(parcel: Parcel) : this() {
        groupId = parcel.readString()
        groupName = parcel.readString()
        groupPhotoId = parcel.readString()
        adminUid = parcel.readString()
    }

    constructor(groupId: String, groupName: String, groupPhotoId: String, participants: ArrayList<String>, adminUid: String) : this() {
        this.groupId = groupId
        this.groupName = groupName
        this.groupPhotoId = groupPhotoId
        this.participants = participants
        this.adminUid = adminUid
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(groupId)
        parcel.writeString(groupName)
        parcel.writeString(groupPhotoId)
        parcel.writeString(adminUid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Group> {
        override fun createFromParcel(parcel: Parcel): Group {
            return Group(parcel)
        }

        override fun newArray(size: Int): Array<Group?> {
            return arrayOfNulls(size)
        }
    }
}