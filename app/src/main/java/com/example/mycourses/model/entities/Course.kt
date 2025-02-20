package com.example.mycourses.model.entities

import android.net.Uri
import java.util.UUID

data class Course(
    val id: String = UUID.randomUUID().toString(),
    val name: String =  "",
    val category: String =  "",
    val price: Double = 0.0,
    val description: String = "",
    val image: String? = null,
    val rate: Double = 0.0,
    val instructorId: String = ""
)

