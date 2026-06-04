package com.mvl.app.presentation.screen2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NicknameScreen(
    which: String,
    viewModel: MapViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val location = viewModel.getLocation(which)
    var nicknameInput by remember { mutableStateOf(location?.nickname ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Location Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp)
                .fillMaxSize()
        ) {
            // Header: A or B + address name
            Text(
                text = which.uppercase(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = location?.name ?: "--",
                fontSize = 16.sp,
                color = Color.DarkGray
            )

            Spacer(Modifier.height(16.dp))

            // AQI row
            Row {
                Text("aqi: ", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = location?.aqi?.let { if (it < 0) "--" else it.toString() } ?: "--",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Nickname (shown if set)
            if (location?.nickname?.isNotBlank() == true) {
                Spacer(Modifier.height(8.dp))
                Row {
                    Text("nickname: ", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Text(location.nickname, fontSize = 15.sp)
                }
            }

            Spacer(Modifier.weight(1f))

            // Nickname input
            OutlinedTextField(
                value = nicknameInput,
                onValueChange = { if (it.length <= 20) nicknameInput = it },
                label = { Text("Nickname (optional, max 20)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("${nicknameInput.length}/20") }
            )

            Spacer(Modifier.height(12.dp))

            // Confirm button
            Button(
                onClick = {
                    viewModel.setNickname(which, nicknameInput.trim())
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Confirm", color = DarkText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
