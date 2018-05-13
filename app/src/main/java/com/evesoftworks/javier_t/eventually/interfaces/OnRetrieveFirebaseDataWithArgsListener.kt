package com.evesoftworks.javier_t.eventually.interfaces

import com.evesoftworks.javier_t.eventually.dbmodel.Event

interface OnRetrieveFirebaseDataWithArgsListener {
    fun onRetrieve(args: String? = null, arrayList: ArrayList<Event>? = null, arrayId: ArrayList<String>? = null, userId: String? = null)
}