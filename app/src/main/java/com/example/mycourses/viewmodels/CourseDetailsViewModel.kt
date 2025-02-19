package com.example.mycourses.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycourses.model.entities.Comment
import com.example.mycourses.model.entities.User
import com.example.mycourses.model.repositories.CourseRepository
import com.example.mycourses.model.repositories.UserRepository
import com.example.mycourses.model.states.CourseDetailsUiState
import com.example.mycourses.model.states.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseDetailsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository
) : ViewModel() {

    var isFavorite by mutableStateOf(false)
        private set

    private val _ratingUpdated = MutableStateFlow(false)
    val ratingUpdated: StateFlow<Boolean> = _ratingUpdated.asStateFlow()

    private val _uiState = MutableStateFlow<CourseDetailsUiState>(CourseDetailsUiState.Loading)
    val uiState: StateFlow<CourseDetailsUiState> = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow<DialogState>(DialogState.None)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    fun initialize(courseId: String) {
        viewModelScope.launch {
            try {
                val user = getUser()
                user?.let {
                    val course = courseRepository.getCourseById(courseId)
                    val isFavorite = user.isFavorite(courseId) ?: false
                    val subscription = userRepository.getUserSubscription(user.id, courseId)
                    val comments = courseRepository.getCommentsForCourse(courseId)
                    val commentsWithUsers = getCommentsWithUsers(comments)
                    val isMyCourse = user.id == course?.instructorId
                    if (course != null) {
                        _uiState.value = CourseDetailsUiState.Success(
                            course,
                            isFavorite,
                            subscription,
                            commentsWithUsers,
                            isMyCourse
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = CourseDetailsUiState.Error(e.message ?: "Erro ao carregar curso")
            }


        }
    }

    private suspend fun getCommentsWithUsers(comments: List<Comment>): List<Pair<Comment, User?>> {
        val commentsWithUsers = mutableListOf<Pair<Comment, User?>>()
        return try {
            comments.forEach { comment ->
                val userId = comment.userId
                val user = userRepository.getUserById(userId)
                commentsWithUsers.add(Pair(comment, user))
            }
            commentsWithUsers
        }catch (ex: Exception) {
            Log.e("CourseDetailsVM", "Erro ao carregar comentários: ${ex.message}")
            emptyList()
        }

    }

    suspend fun getUser(): User? {
        val userId = userRepository.getCurrentUser()
        userId?.let {
            val user = userRepository.getUserById(userId.id)
            return user
        }
        return null
    }

    fun toggleFavorite(courseId: String) {
        viewModelScope.launch {
            if (isFavorite) {
                try {
                    userRepository.unfavoriteCourse(courseId)
                    isFavorite = false
                } catch (e: Exception) {
                    // Tratar erro
                }
            } else {
                try {
                    userRepository.favotireCourse(courseId)
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
                initialize(courseId)
                DialogState.Success("Cadastrado com sucesso!")
            } else {
                DialogState.Error("Erro ao se cadastrar no curso: $result.exceptionOrNull()?.message")
            }

        }
    }

    fun dismissDialog() {
        _dialogState.value = DialogState.None
    }

    fun addComment(userId: String, courseId: String, commentText: String) {
        viewModelScope.launch {
            try {
                courseRepository.addComment(userId, courseId, commentText)
            } catch (e: Exception) {
                Log.e("CourseDetailsVM", "Erro ao adicionar comentário: ${e.message}")
            }
        }
    }
}