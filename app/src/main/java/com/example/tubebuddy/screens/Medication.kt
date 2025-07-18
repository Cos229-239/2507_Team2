package com.example.tubebuddy.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubebuddy.ui.components.BuddyCard

@Composable
fun MedicationScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column {
            Text(
                text = "Medication Today:",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
            BuddyCard()
            BuddyCard()
            BuddyCard()
            BuddyCard()
//            Card(
//                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
//                modifier = Modifier
//                    .size(width = 360.dp, height = 360.dp),
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.primary
//                )
//            ) {
//
//            }
        }
    }
}