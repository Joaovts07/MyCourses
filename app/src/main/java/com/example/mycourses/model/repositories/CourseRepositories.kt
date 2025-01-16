package com.example.mycourses.model.repositories

import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.getCourse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CourseRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getEnrolledCourses(userId: String): List<Course> {
        val subscriptions = firestore.collection("subscription")
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(Course::class.java)

        return subscriptions.mapNotNull { it.id }.map { courseId ->
            getCourseById(courseId)
        }
    }

    suspend fun getCourseById(courseId: String): Course {
        val document = firestore.collection("courses").document(courseId).get().await()
        return getCourse(document)
    }
}