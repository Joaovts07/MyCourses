package com.example.mycourses.viewmodels

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.repositories.CourseRepository
import com.example.mycourses.model.repositories.UserRepository
import com.example.mycourses.model.states.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseCreationViewModel @Inject constructor(
    private val repository: CourseRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(Course())
    val uiState: StateFlow<Course> = _uiState

    private val _dialogState = MutableStateFlow<DialogState>(DialogState.None)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    private val _imageUri = mutableStateOf<Uri?>(null)
    val imageUri: State<Uri?> = _imageUri

    fun loadCourse(courseId: String) {
        viewModelScope.launch {
            val course = repository.getCourseById(courseId)
            _uiState.value = course ?: Course()
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(name = title)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updateCourseImage(imageUri: Uri?) {
        _imageUri.value = imageUri
        _uiState.value = _uiState.value.copy(image = imageUri?.toString() ?: "")
    }

    fun updateCourseInfo(course: Course) {
        _uiState.value = _uiState.value.copy(
            name = course.name,
            description = course.description,
            category = course.category
        )
    }

    fun submitCourse() {
        viewModelScope.launch {
            _dialogState.value = DialogState.Loading
            val userId = userRepository.getUserID()
            _uiState.value = _uiState.value.copy(instructorId = userId)

            val result = repository.createCourse(_uiState.value, _imageUri.value)
            _dialogState.value = if (result.isSuccess) {
                DialogState.Success("Cadastrado com sucesso!")
            } else {
                DialogState.Error("Erro ao cadastrar curso: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun updateCourse() {
        viewModelScope.launch {
            _dialogState.value = DialogState.Loading
            val result = repository.updateCourse(_uiState.value)
            _dialogState.value = if (result.isSuccess) {
                DialogState.Success("Curso atualizado com sucesso!")
            } else {
                DialogState.Error("Erro ao atualizar curso: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun dismissDialog(back: () -> Unit) {
        _dialogState.value = DialogState.None
        back()
    }
}


