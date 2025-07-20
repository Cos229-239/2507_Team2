package com.example.tubebuddy.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubebuddy.ui.components.Entry
import com.example.tubebuddy.ui.components._entryLog
import com.example.tubebuddy.ui.components._schedule

@Composable
fun FeedingLogScreen() {

    var logTappedCard by remember { mutableStateOf<Entry?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        //if log is empty display basic text
        if (!(_entryLog.size >= 1))
            Text(text = "No Logged Items", fontSize = 30.sp, color = Color.Black)
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //displays each entry in log
                items(_entryLog) { entry ->
                    ScheduleBuddyCard(
                        entry._time.hour.toString(),
                        entry._title,
                        entry._type.toString(),
                        modifier = Modifier
                            .size(width = 380.dp, height = 94.dp)
                            .padding(bottom = 18.dp)
                            .clickable { logTappedCard = entry })
                }
            }
        }
    }
}