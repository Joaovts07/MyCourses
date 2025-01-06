package com.example.mycourses.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val age: String = "",
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
}
