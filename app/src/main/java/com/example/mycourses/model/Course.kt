package com.example.mycourses.model

import com.example.mycourses.extensions.toBrazilianCurrency
import com.google.firebase.firestore.DocumentSnapshot
import java.math.BigDecimal
import java.util.UUID

data class Course(
    val id: String = UUID.randomUUID().toString(),
    val name: String =  "",
    val price: String = "",
    val description: String,
    val image: String? = null
)

fun getCourse(document: DocumentSnapshot) = Course(
    id = document.id,
    name = document["name"] as String,
    description = document["description"] as String,
    image = document["image"] as String?,
    price = BigDecimal(document["price"].toString()).toBrazilianCurrency(),
)