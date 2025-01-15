package com.example.mycourses.model.entities

import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
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

    fun getYears(): Int {
        val format = SimpleDateFormat("yyyy", Locale("pt", "BR"))
        val year = format.format(this.age)
        return Calendar.getInstance().get(Calendar.YEAR) - year.toInt()
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