package com.example.mycourses.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.repositories.CourseRepository
import com.example.mycourses.model.states.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseCreationViewModel @Inject constructor(
    private val repository: CourseRepository
) : ViewModel() {
    var uiState by mutableStateOf(Course())
        private set

    private val _dialogState = MutableStateFlow<DialogState>(DialogState.None)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()


    fun updateTitle(title: String) {
        uiState = uiState.copy(name = title)
    }

    fun updateCategory(category: String) {
        uiState = uiState.copy(category = category)
    }

    fun updateCourseImage(imageUri: Uri?) {
        uiState = uiState.copy(image = imageUri.toString())
    }

    fun updateCourseInfo(title: String, category: String) {
        uiState = uiState.copy(name = title, category = category)
    }

    fun submitCourse() {
        viewModelScope.launch {
            try {
                repository.createCourse(uiState)

            } catch (e: Exception){

            }
        }
    }

    fun dismissDialog() {
        _dialogState.value = DialogState.None
    }

}


