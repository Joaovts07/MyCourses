package com.example.mycourses.model.repositories

import android.net.Uri
import android.util.Log
import com.example.mycourses.model.entities.Comment
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.EnrolledCourse
import com.example.mycourses.model.entities.Subscription
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date

class CourseRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
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
                if (course != null) {
                    enrolledCourses.add(EnrolledCourse(subscriptionId, course, subscription.rating))
                }
            }
        }

        emit(enrolledCourses)
    }

    fun getMyCourses(userId: String): Flow<List<Course>> = flow {
        val coursesSnapshot = firestore.collection("courses")
            .whereEqualTo("instructor", userId)
            .get()
            .await()

        val myCourses = mutableListOf<Course>()

        for (document in coursesSnapshot.documents) {
            val courseId = document.id
            val course = getCourseById(courseId)
            if (course != null) {
                myCourses.add(course)
            }
        }

        emit(myCourses)
    }

    suspend fun getCourseById(courseId: String): Course? {
        val document = firestore.collection("courses").document(courseId).get().await()
        return document.toObject<Course>()
    }

    suspend fun getHighlightedCourses(): List<Course> {
        return try {
            val documents = firestore.collection("courses").get().await()
            documents.map { document ->
                document.toObject<Course>()
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
                document.toObject<Course>()
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

    suspend fun addComment(userId: String, courseId: String, text: String) {
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

    suspend fun createCourse(course: Course): Result<Boolean> {
        return try {
            val documentReference = firestore.collection("courses").document()
            val courseId = documentReference.id

            val imageUrlResult = uploadCourseImage(course.imageUri, courseId)

            if (imageUrlResult.isSuccess) {
                val imageUrl = imageUrlResult.getOrNull()

                val courseWithImage = course.copy(
                    id = courseId,
                    image = imageUrl,
                    rate = 0.0
                )

                documentReference.set(courseWithImage).await()
                Result.success(true)
            } else {
                val exception = imageUrlResult.exceptionOrNull() ?: throw Exception("Erro ao Cadastrar Curso")
                Result.failure(exception )
            }

        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun uploadCourseImage(imageUri: Uri?, courseId: String): Result<String?> {
        return try {
            if (imageUri != null) {
                val storageRef = storage.reference.child("course_images/${courseId}")
                val uploadTask = storageRef.putFile(imageUri).await()
                val downloadUrl = uploadTask.metadata?.reference?.downloadUrl?.await()?.toString()
                Result.success(downloadUrl)
            } else {
                throw Exception("Erro ao fazer upload da imagem")
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

}