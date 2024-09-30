import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.registrovoz.Model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
public fun UserListScreen(currentUserEmail: String) {  // Parámetro con el email del usuario autenticado
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val database: DatabaseReference = Firebase.database.reference.child("users")

    LaunchedEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                users = snapshot.children.mapNotNull {
                    it.getValue(User::class.java)?.copy(password = "***") // Ocultar contraseña
                }
                loading = false
            }

            override fun onCancelled(databaseError: DatabaseError) {
                error = "Error al cargar usuarios: ${databaseError.message}"
                loading = false
            }
        }
        database.addValueEventListener(listener)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Usuarios Registrados",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            loading -> CircularProgressIndicator()
            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
            users.isEmpty() -> Text("No hay usuarios registrados")
            else -> UserList(users, currentUserEmail, database, onDeleteUser = { deletedUsername ->
                users = users.filter { it.username != deletedUsername }  // Filtrar el usuario eliminado
            })
        }
    }
}

@Composable
fun UserList(
    users: List<User>,
    currentUserEmail: String,
    database: DatabaseReference,
    onDeleteUser: (String) -> Unit
) {
    LazyColumn {
        items(users) { user ->
            UserCard(user, currentUserEmail, database, onDeleteUser)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun UserCard(
    user: User,
    currentUserEmail: String,
    database: DatabaseReference,
    onDeleteUser: (String) -> Unit
) {
    val isAdmin = currentUserEmail == "admin@gmail.com"  // Verificar si el usuario es el admin
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = user.username,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            // Mostrar botón de eliminar solo si el usuario es admin
            if (isAdmin) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        deleteUserByUsername(user.username, database) { success ->
                            if (success) {
                                onDeleteUser(user.username)  // Eliminar usuario de la lista
                            } else {
                                // Manejar el error (opcional)
                            }
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar usuario",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

fun deleteUserByUsername(username: String, database: DatabaseReference, callback: (Boolean) -> Unit) {
    // Realiza una consulta para buscar al usuario basado en su nombre de usuario
    database.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                // Recorre los resultados (aunque debería haber solo uno)
                for (userSnapshot in snapshot.children) {
                    // Elimina el nodo completo del usuario
                    userSnapshot.ref.removeValue().addOnCompleteListener { task ->
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
