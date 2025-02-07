package com.example.login.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkIfUserExists(userId: String? = null): Boolean {
        return try {
            val userUid = userId ?: auth.currentUser?.uid ?: ""
            val document = firestore.collection("users").document(userUid).get().await()
            document.exists()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun createUser(nome: String, email: String, dateBirthday: Timestamp?) {
        val userId = auth.currentUser?.uid ?: return
        val usersRef = firestore.collection("users").document(userId)

        val newUser = hashMapOf(
            "id" to userId,
            "name" to nome,
            "email" to email,
            "birthday" to dateBirthday
        )

        try {
            usersRef.set(newUser).await()
        } catch (e: Exception) {
        }
    }

}
