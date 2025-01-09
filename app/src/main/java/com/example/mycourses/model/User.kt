package com.example.mycourses.model

import com.google.firebase.Timestamp
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
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
}

fun serializeUser(user: User): String {
    val gson = Gson()
    return gson.toJson(user)
}

fun deserializeUser(userJson: String): User? {
    val gson = Gson()
    return gson.fromJson(userJson, User::class.java)
}