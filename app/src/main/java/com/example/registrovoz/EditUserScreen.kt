package com.example.registrovoz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun EditUserScreen(navController: NavController, currentUserEmail: String) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Referencia a la base de datos
    val database: DatabaseReference = Firebase.database.reference.child("users")

    // Cargar datos del usuario
    LaunchedEffect(Unit) {
        database.orderByChild("username").equalTo(currentUserEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        firstName = userSnapshot.child("firstName").getValue(String::class.java) ?: ""
                        lastName = userSnapshot.child("lastName").getValue(String::class.java) ?: ""
                    }
                } else {
                    errorMessage = "Usuario no encontrado"
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                errorMessage = "Error al cargar los datos: ${error.message}"
                isLoading = false
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = "Modificar mis datos", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de email deshabilitado
            OutlinedTextField(
                value = currentUserEmail,
                onValueChange = {}, // Sin acción ya que es solo lectura
                label = { Text("Email") },
                enabled = false, // Deshabilitado
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Nueva contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    when {
                        firstName.isEmpty() || lastName.isEmpty() -> {
                            errorMessage = "Nombre y apellido no pueden estar vacíos"
                        }
                        password != confirmPassword -> {
                            errorMessage = "Las contraseñas no coinciden"
                        }
                        else -> {
                            // Realiza la actualización del usuario
                            updateUserByUsername(currentUserEmail, database, firstName, lastName, password) { success ->
                                if (success) {
                                    navController.popBackStack()
                                } else {
                                    errorMessage = "Error al actualizar los datos"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Guardar cambios")
            }
        }
    }
}

// Función para actualizar el usuario
fun updateUserByUsername(username: String, database: DatabaseReference, firstName: String, lastName: String, password: String, callback: (Boolean) -> Unit) {
    database.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                for (userSnapshot in snapshot.children) {
                    // Crea un mapa con los datos actualizados
                    val updatedUser = mapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "password" to password // Ten en cuenta que esto no es seguro
                    )
                    userSnapshot.ref.updateChildren(updatedUser).addOnCompleteListener { task ->
                        callback(task.isSuccessful)
                    }
                }
            } else {
                callback(false) // Usuario no encontrado
            }
        }

        override fun onCancelled(error: DatabaseError) {
            callback(false) // Error al consultar
        }
    })
}
