package com.example.mycourses.model

import com.google.firebase.Timestamp
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val age: Timestamp = Timestamp.now(),
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
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        return formato.format(this.age.toDate())
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