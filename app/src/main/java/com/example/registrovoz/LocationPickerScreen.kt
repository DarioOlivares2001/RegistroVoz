import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.registrovoz.Repository.FirebaseRepository
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@Composable
fun LocationPickerScreen(
    navController: NavController,
    username: String
) {
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isMapReady by remember { mutableStateOf(false) }
    val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    val firebaseRepository = remember { FirebaseRepository() }
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                getLastKnownLocation(fusedLocationProviderClient) { location ->
                    currentLocation = LatLng(location.latitude, location.longitude)
                    isMapReady = true
                }
            } else {
                errorMessage = "Se requieren permisos de ubicación para usar esta función."
            }
        }
    )

    LaunchedEffect(Unit) {
        when {
            !isGooglePlayServicesAvailable(context) -> {
                errorMessage = "Los servicios de Google Play no están disponibles."
            }
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLastKnownLocation(fusedLocationProviderClient) { location ->
                    currentLocation = LatLng(location.latitude, location.longitude)
                    isMapReady = true
                }
            }
            else -> {
                launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        if (isMapReady && currentLocation != null) {
            GoogleMap(
                modifier = Modifier.weight(1f),
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(currentLocation!!, 15f)
                },
                onMapClick = { latLng ->
                    selectedLocation = latLng
                }
            ) {
                selectedLocation?.let { location ->
                    Marker(
                        state = MarkerState(position = location),
                        title = "Ubicacion Seleccionada"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    selectedLocation?.let { latLng ->
                        val userLocation = mapOf(
                            "latitude" to latLng.latitude,
                            "longitude" to latLng.longitude
                        )
                        coroutineScope.launch {
                            try {
                                val success = firebaseRepository.saveLocationByUsername(username, userLocation)
                                if (success) {
                                    navController.navigate("home/$username")
                                } else {
                                    errorMessage = "Error al guardar la ubicación." + userLocation
                                }
                            } catch (e: Exception) {
                                errorMessage = "Error: ${e.localizedMessage}"
                            }
                        }
                    }
                },
                enabled = selectedLocation != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Guardar Ubicación")
            }
        } else {
            CircularProgressIndicator()
        }
    }
}

@SuppressLint("MissingPermission")
fun getLastKnownLocation(fusedLocationProviderClient: FusedLocationProviderClient, callback: (Location) -> Unit) {
    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            callback(it)
        }
    }.addOnFailureListener { e ->
        Log.e("LocationPicker", "Error getting location", e)
    }
}

fun isGooglePlayServicesAvailable(context: Context): Boolean {
    val googleApiAvailability = GoogleApiAvailability.getInstance()
    val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
    return resultCode == ConnectionResult.SUCCESS
}