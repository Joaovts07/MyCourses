package com.example.mycourses.model.states

import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.EnrolledCourse
import com.example.mycourses.model.entities.User

sealed class AccountCoursesState {
    object Loading : AccountCoursesState()
    data class Success(
        val enrolledCourses: List<EnrolledCourse>,
        val myCourses: List<Course>,
        val user: User
    ) : AccountCoursesState()
    data class Error(val message: String) : AccountCoursesState()
}