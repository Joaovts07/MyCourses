package com.example.mycourses.model.states

import com.example.mycourses.model.entities.Comment
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.Subscription
import com.example.mycourses.model.entities.User

sealed class CourseDetailsUiState {
    object Loading : CourseDetailsUiState()
    data class Success(
        val course: Course,
        val isFavorite: Boolean,
        val subscription: Subscription?,
        val commentsWithUsers: List<Pair<Comment, User?>>,
        val isMyCourse: Boolean
    ) : CourseDetailsUiState()

    data class Error(val message: String) : CourseDetailsUiState()
}
