package com.tubebuddy.app.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import androidx.core.content.edit

@Composable
fun ScheduleBuddyCard(dateTime: String, title: String, description: String, modifier: Modifier = Modifier) {

    var displayTime by remember { mutableStateOf(dateTime.toInt()) }
    var displayTimeString by remember { mutableStateOf("") }

    if (displayTime > 12){
        displayTimeString = (displayTime-12).toString() + 'p'
    }
    else if (displayTime==12){
        displayTimeString = displayTime.toString() + 'p'
    }
    else if (displayTime==0){
        displayTimeString = "12a"
    }
    else{
        displayTimeString = displayTime.toString() + 'a'
    }

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
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    Text(
                        text = displayTimeString,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSecondary,
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

//checks if it is a new day to reset the schedule (except repeat items)
//also updated latest app open to today's date
fun isNewDay(context: Context): Boolean {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val lastOpened = prefs.getString("last_opened_date", null)
    val today = LocalDate.now().toString()

    return if (lastOpened != today) {
        prefs.edit { putString("last_opened_date", today) }
        true
    } else {
        false
    }
}
