package com.example.mycourses.model.entities

import com.example.mycourses.extensions.toBrazilianCurrency
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import java.math.BigDecimal
import java.util.UUID

data class Course(
    val id: String = UUID.randomUUID().toString(),
    val name: String =  "",
    val price: String = "",
    val description: String,
    val image: String? = null,
    val rate: String = ""
)

fun getCourse(document: DocumentSnapshot) = Course(
    id = document.id,
    name = document["name"] as String,
    description = document["description"] as String,
    image = document["image"] as String?,
    price = BigDecimal(document["price"].toString()).toBrazilianCurrency(),
    rate = document["rate"].toString()
)

fun serializeCourse(course: Course): String {
    val gson = Gson()
    return gson.toJson(course)
}

fun deserializeCourse(courseJson: String): Course? {
    val gson = Gson()
    return gson.fromJson(courseJson, Course::class.java)
}
