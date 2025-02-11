package com.example.mycourses.model.repositories

import android.util.Log
import com.example.mycourses.model.entities.Comment
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.EnrolledCourse
import com.example.mycourses.model.entities.Subscription
import com.example.mycourses.model.entities.getCourse
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date

class CourseRepository(
    private val firestore: FirebaseFirestore
) {

    fun getEnrolledCourses(userId: String): Flow<List<EnrolledCourse>> = flow {
        val subscriptionsSnapshot = firestore.collection("subscriptions")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        val enrolledCourses = mutableListOf<EnrolledCourse>()

        for (document in subscriptionsSnapshot.documents) {
            val subscriptionId = document.id
            val subscription = document.toObject(Subscription::class.java)
            val courseId = subscription?.courseId

            if (courseId != null) {
                val course = getCourseById(courseId)
                enrolledCourses.add(EnrolledCourse(subscriptionId, course, subscription.rating))
            }
        }

        emit(enrolledCourses)
    }

    private suspend fun getCourseById(courseId: String): Course {
        val document = firestore.collection("courses").document(courseId).get().await()
        return getCourse(document)
    }

    suspend fun getHighlightedCourses(): List<Course> {
        return try {
            val documents = firestore.collection("courses").get().await()
            documents.map { document ->
                getCourse(document)
            }
        } catch (e: Exception) {
            Log.e("CourseRepository", "Erro ao buscar cursos em destaque", e)
            emptyList()
        }
    }

    suspend fun getFavoriteCourses(favoriteCoursesIds: Map<String, Boolean>): List<Course> {
        if (favoriteCoursesIds.isEmpty()) return emptyList()

        return try {
            val documents = firestore.collection("courses")
                .whereIn(com.google.firebase.firestore.FieldPath.documentId(), favoriteCoursesIds.keys.toList())
                .get()
                .await()
            documents.map { document ->
                getCourse(document)
            }
        } catch (e: Exception) {
            Log.e("CourseRepository", "Erro ao buscar cursos favoritos", e)
            emptyList()
        }
    }

    suspend fun updateRating(subscriptionId: String, newRating: Float) {
        try {
            firestore.collection("subscriptions")
                .document(subscriptionId).update("rate", newRating).await()

        } catch (e: Exception) {
            Log.e("CourseRepository", "Erro ao Avaliar curso", e)
        }
    }

    suspend fun addComment(courseId: String, userId: String, text: String) {
        val comment = Comment(
            id = firestore.collection("comments").document().id,
            courseId = courseId,
            userId = userId,
            text = text,
            createdAt = Date()
        )
        comment.id?.let {
            firestore.collection("comments").document(it).set(comment).await()
        }
    }

    suspend fun getCommentsForCourse(courseId: String): List<Comment> {
        return try {
            firestore.collection("comments")
                .whereEqualTo("courseId", courseId)
                .get()
                .await()
                .documents.mapNotNull { it.toObject<Comment>() }
        } catch (e: Exception) {
            Log.e("CourseRepository", "Erro ao buscar comentários", e)
            emptyList()
        }
    }
}