package com.example.mycourses.model.states

import com.example.mycourses.model.entities.Subscription

sealed class SubscriptionState {
    data object NoSubscription : SubscriptionState()
    data class Success(val subscription: Subscription?) : SubscriptionState()
    data class Error(val message: String) : SubscriptionState()
}