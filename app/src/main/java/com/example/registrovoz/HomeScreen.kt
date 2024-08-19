package com.example.registrovoz

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.platform.LocalContext
import java.util.*

@Composable
fun HomeScreen(onLoginClick: () -> Unit, onViewUsersClick: () -> Unit) {
    val context = LocalContext.current
    var userList by remember { mutableStateOf(users) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var selectedVowel by remember { mutableStateOf<String?>(null) }

    // Inicializar Text-to-Speech
    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                // Configurar el idioma a español
                val languageResult = tts?.setLanguage(Locale("es", "ES"))
                if (languageResult == TextToSpeech.LANG_MISSING_DATA || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Manejo de errores en caso de que el idioma no esté disponible
                    //LoginScreen(navController = ) .e("TextToSpeech", "Idioma español no soportado o faltan datos.")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Botón para ver usuarios registrados
        Button(
            onClick = {
                onViewUsersClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Ver Usuarios Registrados")
        }

        Text(
            text = "Pincha una Vocal",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Mostrar botones de vocales
        val vowels = listOf("A", "E", "I", "O", "U")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            vowels.forEach { vowel ->
                Button(
                    onClick = {
                        selectedVowel = vowel
                        tts?.speak(vowel, TextToSpeech.QUEUE_FLUSH, null, null)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                ) {
                    Text(text = vowel, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mostrar la imagen de la seña correspondiente a la vocal seleccionada
        selectedVowel?.let { vowel ->
            Image(
                painter = painterResource(id = when (vowel) {
                    "A" -> R.drawable.sign_a
                    "E" -> R.drawable.sign_e
                    "I" -> R.drawable.sign_i
                    "O" -> R.drawable.sign_o
                    "U" -> R.drawable.sign_u
                    else -> R.drawable.sign_a // Imagen predeterminada
                }),
                contentDescription = "Seña de la vocal $vowel",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
            )
        } ?: run {
            Text(
                text = "Selecciona una vocal para ver la seña",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón para ir al login
        Button(onClick = {
            onLoginClick()
        }) {
            Text("Salir")
        }
    }

    // Liberar recursos de Text-to-Speech al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }
}

