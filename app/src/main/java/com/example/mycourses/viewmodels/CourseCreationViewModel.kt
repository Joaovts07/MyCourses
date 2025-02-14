package com.example.mycourses.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycourses.model.repositories.CourseRepository
import com.example.mycourses.model.states.CourseCreationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CourseCreationStep {
    object Info : CourseCreationStep()
    object Image : CourseCreationStep()
    object Review : CourseCreationStep()
}

@HiltViewModel
class CourseCreationViewModel @Inject constructor(
    private val repository: CourseRepository
) : ViewModel() {
    var uiState by mutableStateOf(CourseCreationUiState())
        private set

    var currentStep by mutableStateOf<CourseCreationStep>(CourseCreationStep.Info)
        private set

    fun updateTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun updateCategory(category: String) {
        uiState = uiState.copy(category = category)
    }

    fun updateCourseImage(imageUri: Uri?) {
        uiState = uiState.copy(imageUri = imageUri)
    }

    fun updateCourseInfo(title: String, category: String) {
        uiState = uiState.copy(title = title, category = category)
    }

    fun nextStep() {
        currentStep = when (currentStep) {
            CourseCreationStep.Info -> CourseCreationStep.Image
            CourseCreationStep.Image -> CourseCreationStep.Review
            CourseCreationStep.Review -> CourseCreationStep.Review
        }
    }

    fun previousStep() {
        currentStep = when (currentStep) {
            CourseCreationStep.Review -> CourseCreationStep.Image
            CourseCreationStep.Image -> CourseCreationStep.Info
            CourseCreationStep.Info -> CourseCreationStep.Info
        }
    }

    fun submitCourse() {
        viewModelScope.launch {
            repository.createCourse(uiState.title, uiState.category, uiState.imageUri)
        }
    }

}


