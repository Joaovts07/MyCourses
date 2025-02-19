package com.example.mycourses.model.states

import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.EnrolledCourse

sealed class AccountCoursesState {
    object Loading : AccountCoursesState()
    data class Success(
        val enrolledCourses: List<EnrolledCourse>,
        val myCourses: List<Course>
    ) : AccountCoursesState()
    data class Error(val message: String) : AccountCoursesState()
}