package com.example.mycourses.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.repositories.CourseRepository
import com.example.mycourses.model.repositories.UserRepository
import com.example.mycourses.model.states.CourseUiState
import com.example.mycourses.model.states.DialogState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class CourseDetailsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val courseRepository: CourseRepository
) : ViewModel() {

    var isFavorite by mutableStateOf(false)
        private set

    private val _ratingUpdated = MutableStateFlow(false)
    val ratingUpdated: StateFlow<Boolean> = _ratingUpdated.asStateFlow()

    private val _uiState = MutableStateFlow<CourseUiState>(CourseUiState.Loading)
    val uiState: StateFlow<CourseUiState> = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow<DialogState>(DialogState.None)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    fun initialize(course: Course) {
        viewModelScope.launch {
            val userId = firebaseAuth.currentUser?.uid ?: ""
            val user = userRepository.getUserById(userId)
            val isFavorite = user?.isFavorite(course.id) ?: false
            val subscription = userRepository.getUserSubscription(userId, course.id)
            _uiState.value = CourseUiState.Success(course, isFavorite, subscription)
        }
    }

    fun toggleFavorite(courseId: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val userDocRef = firestore.collection("users").document(userId)

        viewModelScope.launch {
            if (isFavorite) {
                val updates = hashMapOf<String, Any>(
                    "favoriteCourses.$courseId" to FieldValue.delete()
                )
                try {
                    userDocRef.update(updates).await()
                    isFavorite = false
                } catch (e: Exception) {
                    // Tratar erro
                }
            } else {
                try {
                    userDocRef.update("favoriteCourses.$courseId", true).await()
                    isFavorite = true
                } catch (e: Exception) {
                    // Tratar erro
                }
            }
        }
    }

    fun updateRating(subscriptionId: String, newRating: Float) {
        viewModelScope.launch {
            viewModelScope.launch {
                try {
                    courseRepository.updateRating(subscriptionId, newRating)
                    _ratingUpdated.value = true
                } catch (e: Exception) {
                    // Tratar erro
                }
            }
        }
    }
    fun resetRatingUpdated() {
        _ratingUpdated.value = false
    }

    fun subscribeCourse(courseId: String) {
        viewModelScope.launch {
            val result = userRepository.subscribleCourse(courseId)
            _dialogState.value = if (result.isSuccess) {
                DialogState.Success("Cadastrado com sucesso!")
            } else {
                DialogState.Error("Erro ao se cadastrar no curso: $result.exceptionOrNull()?.message")
            }

        }
    }

    fun dismissDialog() {
        _dialogState.value = DialogState.None
    }
}