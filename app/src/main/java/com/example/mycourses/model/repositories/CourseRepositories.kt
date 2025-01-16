package com.example.mycourses.model.repositories

import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.Subscription
import com.example.mycourses.model.entities.getCourse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CourseRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getEnrolledCourses(userId: String): List<Course?> {
        val subscriptions = firestore.collection("subscription")
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(Subscription::class.java)

        return subscriptions.mapNotNull { it.courseId }.map { courseId ->
            getCourseById(courseId)
        }
    }

    private suspend fun getCourseById(courseId: String): Course {
        val document = firestore.collection("courses").document(courseId).get().await()
        return getCourse(document)
    }
}