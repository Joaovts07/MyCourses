package com.example.mycourses.model.states

import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.Subscription

sealed class CourseUiState {
    object Loading : CourseUiState()
    data class Success(
        val course: Course,
        val isFavorite: Boolean,
        val subscription: Subscription?
    ) : CourseUiState()

    data class Error(val message: String) : CourseUiState()
}
