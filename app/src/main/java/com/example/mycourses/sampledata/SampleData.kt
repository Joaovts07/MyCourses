package com.example.mycourses.sampledata

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.example.mycourses.model.Course

val sampleCourseWithImage = Course(
    name = "Curso Jetpack Compose",
    price = "9.99",
    description = LoremIpsum(30).values.first(),
    image = "https://picsum.photos/1920/1080"
)

val sampleCourseWithoutImage = Course(
    name = "Curso de Kotlin",
    price = "9.99",
    description = LoremIpsum(30).values.first(),
)

