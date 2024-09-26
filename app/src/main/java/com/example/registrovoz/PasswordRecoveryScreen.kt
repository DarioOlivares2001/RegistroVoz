package com.example.registrovoz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun PasswordRecoveryScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    val database: DatabaseReference = Firebase.database.reference

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Recuperación de Contraseña", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Nueva Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (successMessage.isNotEmpty()) {
            Text(text = successMessage, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (newPassword == confirmPassword) {
                    // Aquí buscamos al usuario en Firebase y actualizamos la contraseña
                    database.child("users").get().addOnSuccessListener { snapshot ->
                        val userNode = snapshot.children.find { it.child("username").value == username }
                        if (userNode != null) {
                            userNode.ref.child("password").setValue(newPassword).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    successMessage = "Contraseña actualizada con éxito!"
                                    errorMessage = ""
                                    // Opcional: Navegar a la pantalla de inicio de sesión
                                    navController.navigate("login")
                                } else {
                                    errorMessage = "Error al actualizar la contraseña"
                                    successMessage = ""
                                }
                            }
                        } else {
                            errorMessage = "Nombre de usuario no encontrado"
                            successMessage = ""
                        }
                    }
                } else {
                    errorMessage = "Las contraseñas no coinciden"
                    successMessage = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Restablecer Contraseña")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Volver a Iniciar Sesión")
        }
    }
}
