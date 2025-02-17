package com.example.mycourses.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.repositories.CourseRepository
import com.example.mycourses.model.states.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseCreationViewModel @Inject constructor(
    private val repository: CourseRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(Course())  // StateFlow para persistir estado
    val uiState: StateFlow<Course> = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow<DialogState>(DialogState.None)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update { it.copy(name = title) }
    }

    fun updateCategory(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun updateCourseImage(imageUri: Uri?) {
        _uiState.update { it.copy(imageUri = imageUri) }
    }

    fun updateCourseInfo(title: String, category: String) {
        _uiState.update { it.copy(name = title, category = category) }
    }

    fun submitCourse() {
        viewModelScope.launch {
            val result = repository.createCourse(_uiState.value)
            _dialogState.value = if (result.isSuccess) {
                DialogState.Success("Cadastrado com sucesso!")
            } else {
                DialogState.Error("Erro ao cadastrar curso: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun dismissDialog() {
        _dialogState.value = DialogState.None
    }
}


