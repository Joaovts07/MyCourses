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
import com.example.mycourses.model.states.CourseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoursesListViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CourseUiState>(CourseUiState.Loading)
    val uiState: StateFlow<CourseUiState> = _uiState.asStateFlow()

    init {
        loadCourses()
    }

    private fun loadCourses() {
        viewModelScope.launch {
            try {
                val courses = courseRepository.getHighlightedCourses()
                _uiState.value = CourseUiState.Success(courses)
            } catch (e: Exception) {
                _uiState.value = CourseUiState.Error(e.message ?: "Erro ao carregar cursos")
            }
        }
    }

    fun loadFavoriteCourses() {
        viewModelScope.launch {
            try {
                _uiState.value = CourseUiState.Loading
                val user = userRepository.getCurrentUser()
                if (user != null) {
                    val favoriteCourses = courseRepository.getFavoriteCourses(user.favoriteCourses)
                    _uiState.value = CourseUiState.Success(favoriteCourses)
                }
            } catch (e: Exception) {
                _uiState.value = CourseUiState.Error(e.message ?: "Erro ao carregar cursos")
            }
        }
    }
}
