package com.example.mycourses.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycourses.model.entities.User
import com.example.mycourses.model.repositories.UserRepository
import kotlinx.coroutines.launch
import java.util.Date

class EditAccountViewModel (
    private val userRepository: UserRepository,
) : ViewModel() {

    var editedUser by mutableStateOf(User())
        private set

    fun initialize(user: User) {
        editedUser = user.copy()
    }

    fun updateName(name: String) {
        editedUser = editedUser.copy(name = name)
    }

    fun updateEmail(email: String) {
        editedUser = editedUser.copy(email = email)
    }

    fun updateAge(age: Date) {
        editedUser = editedUser.copy(age = age)
    }

    fun saveChanges(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(editedUser)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}