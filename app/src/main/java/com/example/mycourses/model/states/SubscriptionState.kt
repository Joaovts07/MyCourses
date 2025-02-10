package com.example.mycourses.model.states

import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.Subscription

sealed class SubscriptionState {
    data object NoSubscription : SubscriptionState()
    data class Success(val subscription: Subscription?) : SubscriptionState()
    data class Error(val message: String) : SubscriptionState()
}

sealed class CourseUiState {
    object Loading : CourseUiState()
    data class Success(
        val course: Course,
        val isFavorite: Boolean,
        val subscription: Subscription?
    ) : CourseUiState()

    data class Error(val message: String) : CourseUiState()
}

sealed class DialogState {
    object None : DialogState()
    data class Success(val message: String) : DialogState()
    data class Error(val message: String) : DialogState()
}