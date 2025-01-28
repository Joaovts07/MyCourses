package com.example.mycourses.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class CourseDetailsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    var isFavorite by mutableStateOf(false)
        private set

    fun initialize(course: Course) {
        checkIfCourseIsFavorite(course.id)
    }

    private fun checkIfCourseIsFavorite(courseId: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            isFavorite = user?.isFavorite(courseId) ?: false
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
}