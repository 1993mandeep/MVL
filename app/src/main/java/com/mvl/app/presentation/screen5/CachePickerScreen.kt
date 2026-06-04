package com.mvl.app.presentation.screen5

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mvl.app.domain.model.LocationPoint

private val DarkText = Color(0xFF1A1A1A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CachePickerScreen(
    which: String,          // "a" or "b" — which slot we're filling
    viewModel: CachePickerViewModel = hiltViewModel(),
    onLocationSelected: (LocationPoint) -> Unit,
    onBack: () -> Unit
) {
    val locations by viewModel.cachedLocations.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select ${which.uppercase()} Location") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (locations.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No saved locations yet", color = Color.Gray, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Set a location on the map first to cache it here.",
                        color = Color.LightGray,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(locations, key = { "${it.latitude},${it.longitude}" }) { point ->
                    CachedLocationItem(
                        point = point,
                        onClick = { onLocationSelected(point) }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun CachedLocationItem(point: LocationPoint, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // Show nickname if set, otherwise show address
            val displayName = point.nickname.ifBlank { point.name }
            Text(displayName, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = DarkText)

            // If nickname is set, show address as subtitle
            if (point.nickname.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(point.name, fontSize = 12.sp, color = Color.Gray)
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = "aqi: ${if (point.aqi < 0) "--" else point.aqi}",
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}
