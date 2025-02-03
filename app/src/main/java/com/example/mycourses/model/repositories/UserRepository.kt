package com.example.mycourses.model.repositories

import android.net.Uri
import android.util.Log
import com.example.mycourses.model.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

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

            val photoUrl = imageUri?.let { uploadUserPhoto(userId, it) }

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
            val firebaseStorage = FirebaseStorage.getInstance()
            val storageRef = firebaseStorage.reference.child("users/$userId/profile.jpg")
            val uploadTask = storageRef.putFile(imageUri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao fazer upload da foto", e)
            null
        }
    }

}