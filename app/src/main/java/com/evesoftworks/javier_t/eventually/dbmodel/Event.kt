package com.evesoftworks.javier_t.eventually.dbmodel

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class Event() : Parcelable {
    lateinit var eventId: String
    lateinit var placeId: String
    lateinit var category: String
    lateinit var name: String
    lateinit var description: String
    lateinit var latLng: LatLng
    lateinit var eventDate: String
    lateinit var tags: List<String>

    constructor(parcel: Parcel) : this() {
        eventId = parcel.readString()
        placeId = parcel.readString()
        category = parcel.readString()
        name = parcel.readString()
        description = parcel.readString()
        latLng = parcel.readParcelable(LatLng::class.java.classLoader)
        eventDate = parcel.readString()
        tags = parcel.createStringArrayList()
    }

    constructor(eventId: String, category: String, latLng: LatLng, name: String, description: String, placeId: String, eventDate: String, tags: List<String>) : this() {
        this.eventId = eventId
        this.category = category
        this.latLng = latLng
        this.name = name
        this.description = description
        this.placeId = placeId
        this.eventDate = eventDate
        this.tags = tags
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(eventId)
        parcel.writeString(placeId)
        parcel.writeString(category)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeParcelable(latLng, flags)
        parcel.writeString(eventDate)
        parcel.writeStringList(tags)
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