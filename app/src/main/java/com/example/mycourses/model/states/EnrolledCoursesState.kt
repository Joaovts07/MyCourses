package com.example.mycourses.model.states

import com.example.mycourses.model.entities.EnrolledCourse

sealed class EnrolledCoursesState {
    object Loading : EnrolledCoursesState()
    data class Success(val enrolledCourses: List<EnrolledCourse>) : EnrolledCoursesState()
    data class Error(val message: String) : EnrolledCoursesState()
}