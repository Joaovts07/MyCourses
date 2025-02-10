package com.example.mycourses.model.repositories

import android.net.Uri
import android.util.Log
import com.example.mycourses.model.entities.Subscription
import com.example.mycourses.model.entities.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import kotlin.Result
class UserRepository (
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun getCurrentUser(): User? {
        val userId = auth.currentUser?.uid ?: return null
        return getUserById(userId)
    }

    suspend fun getUserById(userId: String): User? {
        val document = firestore.collection("users").document(userId).get().await()
        return document.toObject<User>()
    }

    suspend fun updateUser(user: User, imageUri: Uri? = null): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false

            val photoUrl = imageUri?.let {
                val url = uploadUserPhoto(userId, it)
                url ?: return false
            }

            val updatedUser = user.copy(profilePictureUrl = photoUrl ?: user.profilePictureUrl)

            firestore.collection("users").document(userId).set(updatedUser).await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao atualizar usuário", e)
            false
        }
    }

    private suspend fun uploadUserPhoto(userId: String, imageUri: Uri): String? {
        return try {
            val storageRef = Firebase.storage.reference.child("user_photos/${userId}.jpg")
            val uploadTask = storageRef.putFile(imageUri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("Upload", "Erro ao fazer upload da foto", e)
            null
        }
    }

    suspend fun getUserSubscription(userId: String, courseId: String): Subscription? {
        return try {
            val querySnapshot = firestore.collection("subscription")
                .whereEqualTo("userId", userId)
                .whereEqualTo("courseId", courseId)
                .get()
                .await()
            val subscription = querySnapshot.documents.firstOrNull()?.toObject(Subscription::class.java)
            subscription
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao obter inscrição do usuário", e)
            null
        }

   }

    suspend fun subscribleCourse(courseId: String): Result<Boolean> {
        return try {
            val userId = auth.currentUser?.uid ?:  ""
            val subscription = Subscription(
                id = "",
                userId = userId,
                courseId = courseId,
                rating = "0.0"
            )

            val documentReference = firestore.collection("subscriptions").document()
            val subscriptionWithId = subscription.copy(id = documentReference.id)

            documentReference.set(subscriptionWithId).await()
            Result.success(true)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}