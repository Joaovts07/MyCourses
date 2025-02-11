package com.example.mycourses.model.states

sealed class DialogState {
    object None : DialogState()
    data class Success(val message: String) : DialogState()
    data class Error(val message: String) : DialogState()
}