package com.example.mycourses.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.repositories.CourseRepository
import com.example.mycourses.model.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoursesListViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var courses = mutableStateListOf<Course>()
        private set
    var favoriteCourses = mutableStateListOf<Course>()
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)

    init {
        loadCourses()
    }

    private fun loadCourses() {
        if (isLoading) return
        viewModelScope.launch {
            try {
                isLoading = true
                courses.clear()
                courses.addAll(courseRepository.getHighlightedCourses())
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun loadFavoriteCourses() {
        viewModelScope.launch {
            try {
                isLoading = true
                favoriteCourses.clear()
                val user = userRepository.getCurrentUser()
                if (user != null) {
                    favoriteCourses.addAll(courseRepository.getFavoriteCourses(user.favoriteCourses))
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
}
