package com.evesoftworks.javier_t.eventually.interfaces

import org.json.JSONObject

interface JsonParseable {
    fun parse(jsonObject: JSONObject): Any
    fun parseResults(jsonObject: JSONObject): List<Any>
}