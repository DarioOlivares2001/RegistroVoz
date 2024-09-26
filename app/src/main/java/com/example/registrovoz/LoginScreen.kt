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
import com.example.registrovoz.Model.User
import com.example.registrovoz.R
import com.example.registrovoz.Repository.FirebaseRepository
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Obtén el CoroutineScope para lanzar corutinas
    val coroutineScope = rememberCoroutineScope()
    val firebaseRepository = FirebaseRepository() // Instancia del repositorio

    // Uso de color de fondo consistente
    val backgroundColor = Color(0xFFEFEFEF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_app),
            contentDescription = "Imagen de Inicio de Sesión",
            modifier = Modifier
                .size(300.dp)
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

        // Mensaje de error con un diseño más limpio
        errorMessage.takeIf { it.isNotEmpty() }?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                isLoading = true
                errorMessage = ""

                coroutineScope.launch {
                    try {
                        val isSuccess = firebaseRepository.authenticateUser(username, password)
                        isLoading = false
                        if (isSuccess) {
                            navController.navigate("home/$username")
                        } else {
                            errorMessage = "Nombre de usuario o contraseña inválidos"
                        }
                    } catch (e: Exception) {
                        isLoading = false
                        errorMessage = "Error de conexión"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(text = "Iniciar sesión")
            }
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
