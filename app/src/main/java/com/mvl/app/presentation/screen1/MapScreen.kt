package com.mvl.app.presentation.screen1

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.mvl.app.domain.model.SetStep

private val YellowPrimary = Color(0xFFFFC300)
private val DarkText = Color(0xFF1A1A1A)

@SuppressLint("UnrememberedMutableState")
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onLabelClick: (String) -> Unit,       // tapped a label that IS already set → nickname
    onUnsetLabelClick: (String) -> Unit,  // tapped a label NOT yet set → cache picker
    onBookClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uiState.markerPosition, 15f)
    }

    // Request location permission on launch
    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@rememberLauncherForActivityResult
            }
            fusedClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        locationLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Detect camera idle → update marker position → fetch AQI
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            viewModel.onCameraMove(cameraPositionState.position.target)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full-screen map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(myLocationButtonEnabled = false)
        ) {
            Marker(
                state = MarkerState(position = cameraPositionState.position.target),
                title = "Selected Location"
            )
        }

        // AQI top-right overlay
        AqiChip(
            aqi = uiState.currentAqi,
            isLoading = uiState.isLoadingAqi,
            modifier = Modifier
                .statusBarsPadding()
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        // Bottom panel: A/B labels + V button
        BottomPanel(
            locationALabel = uiState.locationA?.nickname?.ifBlank { uiState.locationA?.name } ?: "",
            locationBLabel = uiState.locationB?.nickname?.ifBlank { uiState.locationB?.name } ?: "",
            step = uiState.step,
            onALabelClick = {
                if (uiState.locationA != null) onLabelClick("a") else onUnsetLabelClick("a")
            },
            onBLabelClick = {
                if (uiState.locationB != null) onLabelClick("b") else onUnsetLabelClick("b")
            },
            onVButtonClick = {
                if (uiState.step == SetStep.BOOK) {
                    viewModel.performBooking(onSuccess = onBookClick)
                } else {
                    viewModel.onVButtonTapped(onBook = onBookClick)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun AqiChip(aqi: Int, isLoading: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "aqi: ",
                fontSize = 14.sp,
                color = DarkText,
                fontWeight = FontWeight.Medium
            )
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
            } else {
                Text(
                    text = if (aqi < 0) "--" else aqi.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = aqiColor(aqi)
                )
            }
        }
    }
}

private fun aqiColor(aqi: Int) = when {
    aqi < 0 -> Color.Gray
    aqi <= 50 -> Color(0xFF00B050)   // Good
    // Moderate
    aqi <= 150 -> Color(0xFFFF7E00)  // Unhealthy for sensitive
    else -> Color(0xFFFF0000)        // Unhealthy+
}

@Composable
private fun BottomPanel(
    locationALabel: String,
    locationBLabel: String,
    step: SetStep,
    onALabelClick: () -> Unit,
    onBLabelClick: () -> Unit,
    onVButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonLabel = when (step) {
        SetStep.SET_A -> "Set A"
        SetStep.SET_B -> "Set B"
        SetStep.BOOK -> "Book"
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // A Label row
            LocationRow(
                label = "A",
                value = locationALabel.ifBlank { "—" },
                isClickable = true,
                onClick = onALabelClick
            )

            Spacer(Modifier.height(8.dp))

            // B Label row
            LocationRow(
                label = "B",
                value = locationBLabel.ifBlank { "—" },
                isClickable = true,
                onClick = onBLabelClick
            )

            Spacer(Modifier.height(16.dp))

            // V Button (yellow primary)
            Button(
                onClick = onVButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(buttonLabel, color = DarkText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LocationRow(
    label: String,
    value: String,
    isClickable: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isClickable) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = DarkText,
            modifier = Modifier.width(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            color = if (isClickable) MaterialTheme.colorScheme.primary else Color.Gray,
            modifier = Modifier.weight(1f)
        )
    }
}
