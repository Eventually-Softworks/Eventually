package com.evesoftworks.javier_t.eventually.databaseobjects

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import java.sql.Timestamp

class Event() : Parcelable, Serializable {
    lateinit var placeId: String
    lateinit var category: String
    lateinit var name: String
    lateinit var latLng: LatLng

    constructor(category: String, latLng: LatLng, name: String, placeId: String) : this() {
        this.category = category
        this.latLng = latLng
        this.name = name
        this.placeId = placeId
    }

    constructor(parcel: Parcel) : this() {
        placeId = parcel.readString()
        category = parcel.readString()
        name = parcel.readString()
        latLng = parcel.readParcelable(LatLng::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(placeId)
        parcel.writeString(category)
        parcel.writeString(name)
        parcel.writeParcelable(latLng, flags)
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