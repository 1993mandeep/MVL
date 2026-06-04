package com.mvl.app.presentation.screen3

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mvl.app.presentation.screen1.MapViewModel

private val YellowPrimary = Color(0xFFFFC300)
private val DarkText = Color(0xFF1A1A1A)

@Composable
fun BookingConfirmScreen(
    viewModel: MapViewModel,
    onNextClick: () -> Unit,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val booking = uiState.currentBooking

    // Hardware/gesture back → reset to Screen 1
    BackHandler { onBackPressed() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(32.dp))

        Text("Booking Details", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkText)
        Spacer(Modifier.height(24.dp))

        // A info
        SectionHeader("A")
        InfoRow("Address", booking?.a?.name ?: "--")
        InfoRow("AQI", booking?.a?.aqi?.toString() ?: "--")
        val nickA = uiState.locationA?.nickname
        if (!nickA.isNullOrBlank()) InfoRow("Nickname", nickA)

        Spacer(Modifier.height(16.dp))

        // B info
        SectionHeader("B")
        InfoRow("Address", booking?.b?.name ?: "--")
        InfoRow("AQI", booking?.b?.aqi?.toString() ?: "--")
        val nickB = uiState.locationB?.nickname
        if (!nickB.isNullOrBlank()) InfoRow("Nickname", nickB)

        Spacer(Modifier.height(24.dp))
        Divider()
        Spacer(Modifier.height(16.dp))

        // Price
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("price", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(
                text = booking?.price?.let { "%.0f".format(it) } ?: "--",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.weight(1f))

        // Next → History
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("View History", color = DarkText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SectionHeader(label: String) {
    Text(
        text = label,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = DarkText,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun InfoRow(key: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(key, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
