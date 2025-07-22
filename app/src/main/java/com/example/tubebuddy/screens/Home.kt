package com.example.tubebuddy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import kotlin.math.roundToInt

@Composable
fun HomeScreen() {
    val scheduleItems = remember {
        mutableStateListOf("Bolus Feed - 8:00 AM", "Flush - 12:00 PM", "Medication - 3:00 PM")
    }

    val feedingLog = listOf(
        "Medication Completed - 8:00 AM",
        "Flush Completed - 12:00 PM",
        "Bolus Feed Completed - 3:00 PM"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 72.dp)
        ) {

            // SCHEDULE SECTION
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Schedule", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.Black)
                        IconButton(onClick = {

                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add to schedule")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    scheduleItems.forEach { item ->
                        SwipeToDeleteCard(item = item) {
                            scheduleItems.remove(it)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FEEDING LOG SECTION
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Feeding Log", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.Black)

                        // Inline Inventory Alert
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Inventory low",
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(start = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    feedingLog.forEach {
                        Text(
                            text = "• $it",
                            modifier = Modifier.padding(vertical = 4.dp),
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // Floating Add Button
        FloatingActionButton(
            onClick = { /* Add schedule item */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Composable
fun SwipeToDeleteCard(item: String, onDelete: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(item, color = Color.Black)
            TextButton(onClick = { onDelete(item) }) {
                Text("Delete", color = Color.Red)
            }
        }
    }
}

