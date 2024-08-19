package com.example.registrovoz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val backgroundColor = MaterialTheme.colorScheme.background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)  // Aplica el color de fondo
            .padding(16.dp)
            .background(Color(0xFFEFEFEF)), // Cambia el color de fondo aquí
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Añadir la imagen encima del texto "Iniciar sesión"
        Image(
            painter = painterResource(id = R.drawable.logo_app),
            contentDescription = "Imagen de Inicio de Sesión",
            modifier = Modifier
                .size(300.dp)  // Tamaño de la imagen
                .padding(bottom = 16.dp)
        )

        Text(text = "Iniciar sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
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
                val user = users.find { it.first == username && it.second == password }
                if (user != null) {
                    errorMessage = ""
                    // Navegar a la pantalla del menú si el inicio de sesión es exitoso
                    navController.navigate("home")
                } else {
                    errorMessage = "Nombre de usuario o contraseña inválidos"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "¿No tienes una cuenta? Regístrate")
        }

        TextButton(
            onClick = { navController.navigate("password_recovery") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "¿Olvidaste tu contraseña? Recupérala aquí")
        }
    }
}
