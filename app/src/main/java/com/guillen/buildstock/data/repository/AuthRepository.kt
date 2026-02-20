package com.guillen.buildstock.data.repository

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.guillen.buildstock.data.model.User

class AuthRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            android.util.Log.e("AUTH_REPO", "Error en login", e)
            false
        }
    }

    suspend fun getUserProfile(): User? {
        return try {
            val uid = auth.currentUser?.uid ?: return null
            val document = db.collection("users").document(uid).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            android.util.Log.e("AUTH_REPO", "Error obteniendo perfil", e)
            null
        }
    }

    suspend fun getAllUsers(): List<User> {
        return try {
            val snapshot = db.collection("users").get().await()
            snapshot.documents.mapNotNull { it.toObject(User::class.java) }
        } catch (e: Exception) {
            android.util.Log.e("AUTH_REPO", "Error obteniendo usuarios", e)
            emptyList()
        }
    }

    suspend fun registerUserAsAdmin(context: Context, name: String, email: String, password: String, role: String, phone: String): Boolean {
        return try {
            val defaultApp = FirebaseApp.getInstance()
            val options = defaultApp.options

            var secondaryApp = FirebaseApp.getApps(context).firstOrNull { it.name == "AdminCreatorApp" }
            if (secondaryApp == null) {
                secondaryApp = FirebaseApp.initializeApp(context, options, "AdminCreatorApp")
            }

            val secondaryAuth = FirebaseAuth.getInstance(secondaryApp!!)
            val authResult = secondaryAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid

            if (userId != null) {
                val userProfile = hashMapOf<String, Any>(
                    "name" to name,
                    "email" to email,
                    "role" to role,
                    "phone" to phone
                )

                db.collection("users").document(userId).set(userProfile).await()
                secondaryAuth.signOut()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            android.util.Log.e("AUTH_REPO", "Error al crear usuario", e)
            false
        }
    }

    suspend fun updateUserProfile(email: String, newName: String, newRole: String, newPhone: String): Boolean {
        return try {
            val snapshot = db.collection("users").whereEqualTo("email", email).get().await()

            if (!snapshot.isEmpty) {
                val documentId = snapshot.documents[0].id
                val updates = mapOf(
                    "name" to newName,
                    "role" to newRole,
                    "phone" to newPhone
                )
                db.collection("users").document(documentId).update(updates).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            android.util.Log.e("AUTH_REPO", "Error al actualizar", e)
            false
        }
    }

    suspend fun deleteUser(userEmail: String): Boolean {
        return try {
            val snapshot = db.collection("users").whereEqualTo("email", userEmail).get().await()
            if (!snapshot.isEmpty) {
                val documentId = snapshot.documents[0].id
                db.collection("users").document(documentId).delete().await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            android.util.Log.e("AUTH_REPO", "Error al borrar usuario", e)
            false
        }
    }
}