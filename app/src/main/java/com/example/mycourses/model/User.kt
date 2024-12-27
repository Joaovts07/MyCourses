package com.example.mycourses.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    private val _favoriteCourses: MutableMap<String, Boolean> = mutableMapOf()
) {
    val favoriteCourses: Map<String, Boolean>
        get() = _favoriteCourses.toMap()

    fun isFavorite(courseId: String): Boolean {
        return _favoriteCourses[courseId] ?: false
    }

    fun addFavoriteCourse(courseId: String) {
        _favoriteCourses[courseId] = true
    }

    fun removeFavoriteCourse(courseId: String) {
        _favoriteCourses.remove(courseId)
    }
}
