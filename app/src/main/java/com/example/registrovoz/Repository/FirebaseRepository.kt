package com.example.registrovoz.Repository

import com.example.registrovoz.Model.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

public class FirebaseRepository {
    private val database: DatabaseReference = Firebase.database.reference

    suspend fun registerUser(user: User): Boolean {
        return try {
            database.child("users").push().setValue(user).await()
            true // Si se completa correctamente
        } catch (e: Exception) {
            false // En caso de error
        }
    }

    suspend fun authenticateUser(username: String, password: String): Boolean {
        val snapshot = database.child("users").get().await()
        for (userSnapshot in snapshot.children) {
            val user = userSnapshot.getValue(User::class.java)
            if (user != null && user.username == username && user.password == password) {
                return true
            }
        }
        return false
    }
}