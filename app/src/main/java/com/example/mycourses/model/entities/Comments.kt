package com.example.mycourses.model.entities

import kotlinx.serialization.Contextual
import java.util.Date

data class Comment(
    var id: String? = null,
    val courseId: String = "",
    val userId: String = "",
    val text: String = "",
    @Contextual
    val createdAt: Date = Date()
)
