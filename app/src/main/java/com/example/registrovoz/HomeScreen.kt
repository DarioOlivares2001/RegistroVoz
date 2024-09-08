package com.example.registrovoz

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var selectedLetter by remember { mutableStateOf<String?>(null) }

    // Inicializa TTS
    LaunchedEffect(Unit) {
        try {
            tts = TextToSpeech(context) { status ->
                if (status != TextToSpeech.ERROR) {
                    try {
                        val languageResult = tts?.setLanguage(Locale("es", "ES"))
                        if (languageResult == TextToSpeech.LANG_MISSING_DATA || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {

                        }
                    } catch (e: Exception) {

                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "APRENDIENDO LENGUAJE DE SEÑAS",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val alphabet = ('A'..'Z').toList()
            val angleStep = 360f / alphabet.size
            val radius = 180.dp

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {

                val offsetAngle = -90

                alphabet.forEachIndexed { index, letter ->
                    val angleInRadians = Math.toRadians((angleStep * index + offsetAngle).toDouble())
                    val xOffset = (radius.value * cos(angleInRadians)).dp
                    val yOffset = (radius.value * sin(angleInRadians)).dp

                    Button(
                        onClick = {
                            try {
                                selectedLetter = letter.toString()
                                tts?.speak(letter.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
                            } catch (e: Exception) {

                                e.printStackTrace()
                            }
                        },
                        modifier = Modifier
                            .offset(xOffset, yOffset)
                            .size(50.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = letter.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }


                selectedLetter?.let { letter ->
                    Image(
                        painter = painterResource(id = LetterImages.getImageResource(letter)),
                        contentDescription = "Seña de la letra $letter",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(16.dp)
                    )
                } ?: run {
                    Text(
                        text = "Selecciona una letra",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Gray
                    )
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {

                Button(
                    onClick = {
                        try {
                            navController.navigate("login")
                        } catch (e: Exception) {

                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Ir al Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }


                Button(
                    onClick = {
                        try {
                            navController.navigate("user_list")
                        } catch (e: Exception) {

                            e.printStackTrace()
                        }
                    }
                ) {
                    Text(
                        text = "Listado de Usuarios",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }


        DisposableEffect(Unit) {
            onDispose {
                try {
                    tts?.stop()
                    tts?.shutdown()
                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }
        }
    }
}
