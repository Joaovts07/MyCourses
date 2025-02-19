package com.example.mycourses.model.states

import com.example.mycourses.model.entities.Course

sealed class CourseUiState {
    data object Loading : CourseUiState()
    data class Success(
        val courses: List<Course>,
    ) : CourseUiState()

    data class Error(val message: String) : CourseUiState()

}