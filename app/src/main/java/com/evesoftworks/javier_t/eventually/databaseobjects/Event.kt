package com.evesoftworks.javier_t.eventually.databaseobjects

import android.os.Parcel
import android.os.Parcelable
import java.sql.Timestamp

class Event(): Parcelable {
    lateinit var category: String
    lateinit var name: String

    constructor(parcel: Parcel) : this() {
        category = parcel.readString()
        name = parcel.readString()
    }

    constructor(category: String, name: String): this(){
        this.category = category
        this.name = name
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }
    }
}