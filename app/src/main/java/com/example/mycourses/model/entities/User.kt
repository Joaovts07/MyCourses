package com.example.mycourses.model.entities

import com.google.gson.Gson
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    @Contextual
    val age: Date = Date(),
    val profilePictureUrl: String = "",
    val favoriteCourses: MutableMap<String, Boolean> = mutableMapOf()
) {

    fun isFavorite(courseId: String): Boolean {
        return favoriteCourses[courseId] ?: false
    }

    fun addFavoriteCourse(courseId: String) {
        favoriteCourses[courseId] = true
    }

    fun removeFavoriteCourse(courseId: String) {
        favoriteCourses.remove(courseId)
    }

    fun getFormattedAge(): String {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        return format.format(this.age)
    }

    fun getYears(): Int {
        val birthDate = Calendar.getInstance().apply {
            time = age
        }
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }
}

fun serializeUser(user: User): String {
    val gson = Gson()
    return gson.toJson(user)
}

fun deserializeUser(userJson: String): User? {
    val gson = Gson()
    return gson.fromJson(userJson, User::class.java)
}