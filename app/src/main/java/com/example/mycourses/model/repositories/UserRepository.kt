package com.example.mycourses.model.repositories

import android.util.Log
import com.example.mycourses.model.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
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

    suspend fun updateUser(user: User): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            firestore.collection("users").document(userId).set(user).await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao atualizar usuário", e)
            false
        }
    }

}