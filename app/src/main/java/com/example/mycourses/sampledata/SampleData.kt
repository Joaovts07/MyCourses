package com.example.mycourses.sampledata

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.example.mycourses.model.Course
import java.math.BigDecimal
import kotlin.random.Random

private val loremName = LoremIpsum(Random.nextInt(10)).values.first()
private val loremDesc = LoremIpsum(Random.nextInt(30)).values.first()

val sampleCourseWithImage = Course(
    name = "Curso Jetpack Compose",
    price = BigDecimal("9.99"),
    description = LoremIpsum(30).values.first(),
    image = "https://picsum.photos/1920/1080"
)

val sampleCourseWithoutImage = Course(
    name = "Curso de Kotlin",
    price = BigDecimal("9.99"),
    description = LoremIpsum(30).values.first(),
)

val sampleCourses = List(10) { index ->
    Course(
        name = loremName,
        price = BigDecimal("9.99"),
        description = loremDesc,
        image = if (index % 2 == 0) "https://picsum.photos/1920/1080" else null
    )
}

