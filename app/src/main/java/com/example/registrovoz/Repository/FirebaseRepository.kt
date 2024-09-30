package com.example.registrovoz.Repository

import android.util.Log
import com.example.registrovoz.Model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

public open class FirebaseRepository {
    protected open val database: DatabaseReference = Firebase.database.reference

    open suspend fun registerUser(user: User): Boolean {
        return try {
            database.child("users").push().setValue(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    open suspend fun authenticateUser(username: String, password: String): Boolean {
        val snapshot = database.child("users").get().await()
        for (userSnapshot in snapshot.children) {
            val user = userSnapshot.getValue(User::class.java)
            if (user != null && user.username == username && user.password == password) {
                return true
            }
        }
        return false
    }


    open suspend fun saveLocationByUsername(username: String, locationData: Map<String, Double>): Boolean {

        Log.d("FirebaseRepository", "Datos recibidos - Username: $username, LocationData: $locationData")

        return try {
            val snapshot = database.child("users").orderByChild("username").equalTo(username).get().await()

            if (snapshot.exists()) {

                for (userSnapshot in snapshot.children) {

                    userSnapshot.ref.child("location").updateChildren(locationData).await()
                    Log.d("FirebaseRepository", "Ubicación guardada para el usuario: $username")
                }
                true
            } else {
                Log.e("FirebaseRepository", "Usuario no encontrado: $username")
                false
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error al guardar la ubicación: ${e.localizedMessage}", e)
            false
        }
    }



}