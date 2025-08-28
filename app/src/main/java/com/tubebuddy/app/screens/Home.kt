package com.tubebuddy.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tubebuddy.app.ui.components.Entry
import com.tubebuddy.app.ui.components.FeedEntry
import com.tubebuddy.app.ui.components._entryLog
import com.tubebuddy.app.ui.components._schedule
import com.tubebuddy.app.ui.components.isNewDay

@Composable
fun HomeScreen() {

    val homeContext = LocalContext.current

    LaunchedEffect(Unit) {

        if (_schedule.isEmpty() && _entryLog.isEmpty()) {
            loadItemsFromFB()
        }
        if (isNewDay(homeContext)){
            newDayClearCompleteEntries(homeContext)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // SCHEDULE SECTION
            Card(
                modifier = Modifier
                    .fillMaxHeight(.5f)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Schedule Overview",
                            modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground)
//                        IconButton(onClick = { } ) {
//                            Icon(
//                                Icons.Default.Add,
//                                contentDescription = "Add to schedule",
//                                tint = MaterialTheme.colorScheme.primary) // color added here to hide the add button for now
//                        }
                    }

                    if(_schedule.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No Scheduled Items",
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                    else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            items(_schedule) { entry->
                                HomeBuddyCard(
                                    entry,
                                    modifier = Modifier
                                        .size(width = 380.dp, height = 84.dp)
                                        .padding(bottom = 8.dp))


                                        //.clickable { tappedCard = entry })
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FEEDING LOG SECTION
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Log Overview",
                            modifier = Modifier.padding(start = 8.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground)

                        // Inline Inventory Alert
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Inventory Low",
                                modifier = Modifier.padding(end = 4.dp),
                                color = Color.Red,
                                fontSize = 12.sp,
                            )
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    if(_entryLog.isEmpty()){
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No Logged Items",
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                    else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            items(_entryLog) { entry->
                                LogBuddyCard(
                                    entry, modifier = Modifier
                                        .size(width = 380.dp, height = 84.dp)
                                        .padding(bottom = 8.dp))
                            }
                        }
                    }
                }
            }
        }

        // Floating Add Button
//        FloatingActionButton(
//            onClick = { /* Add schedule item */ },
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(24.dp),
//            containerColor = MaterialTheme.colorScheme.surface,
//            contentColor = MaterialTheme.colorScheme.tertiary
//        ) {
//            Text(text = "+", fontSize = 24.sp)
//        }
    }
}

@Composable
fun HomeBuddyCard(entry: Entry, modifier: Modifier = Modifier) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier
                    .size(width = 64.dp, height = 56.dp)
                    .padding(start = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    Text(
                        text = scheduleFormatShortTime(entry._time),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            Column {
                Text(
                    text = entry._title,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Row {
                    Text(
                        text = entry._type.toString(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    if (entry is FeedEntry){
                        Text(
                            text = " • " + entry._feedType.toString(),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}