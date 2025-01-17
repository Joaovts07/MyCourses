package com.example.mycourses.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.User
import com.example.mycourses.model.repositories.CourseRepository
import com.example.mycourses.model.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository
) : ViewModel() {

    var user by mutableStateOf<User?>(null)
        private set
    var editedUser by mutableStateOf(User())
        private set
    var enrolledCourses by mutableStateOf<List<Course?>>(emptyList())
        private set
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadAccountData()
    }

    private fun loadAccountData() {
        viewModelScope.launch {
            try {
                isLoading = true
                user = userRepository.getCurrentUser()
                user?.let {
                    enrolledCourses = courseRepository.getEnrolledCourses(it.id)
                }
            } catch (e: Exception) {
                errorMessage = "Erro ao carregar dados: ${e.message}"
                Log.e("AccountViewModel", "Erro ao carregar dados da conta", e)
            } finally {
                isLoading = false
            }
        }
    }

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

    fun saveChanges(onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(editedUser)
                onSuccess(editedUser)
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

}