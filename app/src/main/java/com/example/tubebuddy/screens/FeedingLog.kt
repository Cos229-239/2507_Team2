package com.example.tubebuddy.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.tubebuddy.ui.components.EntryType
import com.example.tubebuddy.ui.components.MedicationEntry
import com.example.tubebuddy.ui.components._entryLog
import com.example.tubebuddy.ui.components._schedule

//Schedule Detail Sheet
@Composable
fun LogEntryDetailSheet(entry: Entry, onDelete:()->Unit, onDismiss:()->Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.45f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Title: ${entry._title}", color = Color.Black)
            Text("Type: ${entry._type}", color = Color.Black)
            Text("Time: ${entry._time}", color = Color.Black)
            Text("Amount: ${entry._amount} ${entry._unit}", color = Color.Black)
            Text("Notes: ${entry._notes}", color = Color.Black)

            if (entry is MedicationEntry) {
                Text("Medication: ${entry._medicationName}", color = Color.Black)
                Text("Med Type: ${entry._medType}", color = Color.Black)
            }

        }

        Button(
            onClick = onDelete,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White,
            ), modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

    //sheet if item is tapped
    if (logTappedCard!=null){
        ModalBottomSheet(
            onDismissRequest = { logTappedCard = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        ) {
            LogEntryDetailSheet(logTappedCard!!,
                onDelete = {
                    _entryLog.remove(logTappedCard)
                    logTappedCard = null;
                },
                onDismiss = {
                    logTappedCard = null
                })
        }
    }
}