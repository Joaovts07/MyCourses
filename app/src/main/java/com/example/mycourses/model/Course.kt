package com.example.mycourses.model

import java.math.BigDecimal
import java.util.UUID

data class Course(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val price: BigDecimal,
    val description: String,
    val image: String? = null
)