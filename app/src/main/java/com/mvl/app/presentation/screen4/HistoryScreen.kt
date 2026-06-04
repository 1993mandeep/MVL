package com.mvl.app.presentation.screen4

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mvl.app.domain.model.BookingRecord

private val DarkText = Color(0xFF1A1A1A)

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onItemClick: (BookingRecord) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Summary header
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Count", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = uiState.totalCount.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Price", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = "%.0f".format(uiState.totalPrice),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                }
            }
        }

        Divider()

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.records, key = { it.id ?: it.hashCode().toString() }) { record ->
                    BookingRecordItem(record = record, onClick = { onItemClick(record) })
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun BookingRecordItem(record: BookingRecord, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("A", fontWeight = FontWeight.Bold, fontSize = 14.sp,
                modifier = Modifier.width(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(record.a.name, fontSize = 14.sp, color = Color.DarkGray)
        }
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("B", fontWeight = FontWeight.Bold, fontSize = 14.sp,
                modifier = Modifier.width(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(record.b.name, fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}
