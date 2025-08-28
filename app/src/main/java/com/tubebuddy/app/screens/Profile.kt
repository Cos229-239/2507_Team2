package com.tubebuddy.app.screens

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.tubebuddy.app.firebase.AuthViewModel
import com.tubebuddy.app.ui.components.Entry
import com.tubebuddy.app.ui.components.EntryType
import com.tubebuddy.app.ui.components.EntryUnits
import com.tubebuddy.app.ui.components.FeedEntry
import com.tubebuddy.app.ui.components.FeedType
import com.tubebuddy.app.ui.components.FlushEntry
import com.tubebuddy.app.ui.components.MedType
import com.tubebuddy.app.ui.components.MedicationEntry
import com.tubebuddy.app.ui.components._schedule
import androidx.compose.foundation.clickable
import com.tubebuddy.app.ui.theme.ThemeStateHolder
import java.time.LocalDateTime

fun mapTime(timeMap: Map<String, Any>?): LocalDateTime? {
    if(timeMap == null) { return null }

    val year = (timeMap["year"] as? Number)?.toInt() ?: 0
    val month = (timeMap["monthValue"] as? Number)?.toInt() ?: 0
    val day = (timeMap["dayOfMonth"] as? Number)?.toInt() ?: 0
    val hour = (timeMap["hour"] as? Number)?.toInt() ?: 0
    val minute = (timeMap["minute"] as? Number)?.toInt() ?: 0
    val second = (timeMap["second"] as? Number)?.toInt() ?: 0
    Log.d("READ MEE", year.toString() + month.toString())

    if(year == 0 || month == 0 || day == 0) { return null }
    return LocalDateTime.of(year, month, day, hour, minute, second)
}

fun mapEntry(itemMap: Map<String, Any>): Entry? {
    val typeString = itemMap["_type"] as? String ?: return null

    return when (typeString) {
        "FEED" -> {
            val timeMap = itemMap["_time"] as? Map<String, Any>
            val time = mapTime(timeMap) ?: LocalDateTime.now()

            FeedEntry(
                _type = EntryType.FEED,
                _complete = itemMap["_complete"] as? Boolean ?: false,
                _repeats = itemMap["_repeats"] as? Boolean ?: false,
                _title = itemMap["_title"] as? String ?: "",
                _time = time,
                _amount = (itemMap["_amount"] as? Number ?: 0.0).toDouble(),
                _unit = (itemMap["_unit"] as? String)?.let { EntryUnits.valueOf(it) } ?: EntryUnits.mL,
                _notes = itemMap["_notes"] as? String ?: "",
                _feedType = (itemMap["_feedType"] as? String)?.let { FeedType.valueOf(it) } ?: FeedType.ORAL,
                _checked = itemMap["_checked"] as? Boolean ?: false
            )
        }
        "FLUSH" -> {
            val timeMap = itemMap["_time"] as? Map<String, Any>
            val time = mapTime(timeMap) ?: LocalDateTime.now()

            FlushEntry(
                _type = EntryType.FLUSH,
                _complete = itemMap["_complete"] as? Boolean ?: false,
                _repeats = itemMap["_repeats"] as? Boolean ?: false,
                _title = itemMap["_title"] as? String ?: "",
                _time = time,
                _amount = (itemMap["_amount"] as? Number ?: 0.0).toDouble(),
                _unit = (itemMap["_unit"] as? String)?.let { EntryUnits.valueOf(it) } ?: EntryUnits.mL,
                _notes = itemMap["_notes"] as? String ?: "",
                _checked = itemMap["_checked"] as? Boolean ?: false
            )
        }
        "MEDICINE" -> {
            val timeMap = itemMap["_time"] as? Map<String, Any>
            val time = mapTime(timeMap) ?: LocalDateTime.now()

            MedicationEntry(
                _type = EntryType.MEDICINE,
                _complete = itemMap["_complete"] as? Boolean ?: false,
                _repeats = itemMap["_repeats"] as? Boolean ?: false,
                _title = itemMap["_title"] as? String ?: "",
                _time = time,
                _amount = (itemMap["_amount"] as? Number ?: 0.0).toDouble(),
                _unit = (itemMap["_unit"] as? String)?.let { EntryUnits.valueOf(it) } ?: EntryUnits.mg,
                _notes = itemMap["_notes"] as? String ?: "",
                _medType = (itemMap["_medType"] as? String)?.let { MedType.valueOf(it) } ?: MedType.ORAL,
                _medicationName = itemMap["_medicationName"] as? String ?: "",
                _checked = itemMap["_checked"] as? Boolean ?: false
            )
        }
        else -> null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(authViewModel: AuthViewModel) {
    val db = Firebase.firestore
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val usernameState = remember { mutableStateOf<String?>(null) }
    var username by remember { mutableStateOf("")}
    val isDarkTheme by ThemeStateHolder.isDarkTheme
    var showFAQSheet by remember { mutableStateOf(false) }
    var showUsernameSheet by remember { mutableStateOf(false) }
    uid?.let {
        db.collection("users").document(it).get()
            .addOnSuccessListener { document ->
                if(document.exists()) {
                    usernameState.value = document.getString("name")
                }
            }
    }
    if(usernameState.value == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(usernameState.value == null) {
                    CircularProgressIndicator()
                }
                else {
                    Card(
                        modifier = Modifier
                            .fillMaxHeight(.7f)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Text(
                            text = "${usernameState.value}'s Settings",
                            fontSize = 24.sp,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onBackground)

                        // BEGIN DARK MODE
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Toggle Dark Mode",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 16.dp),
                                color = MaterialTheme.colorScheme.onBackground)
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { isChecked ->
                                    ThemeStateHolder.isDarkTheme.value = isChecked
                                },
                                modifier = Modifier.padding(end = 16.dp),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.tertiary,
                                    checkedTrackColor = MaterialTheme.colorScheme.surface,
                                    checkedBorderColor = MaterialTheme.colorScheme.tertiary,
                                    checkedIconColor = MaterialTheme.colorScheme.tertiary,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                                    uncheckedTrackColor = MaterialTheme.colorScheme. onSurface,
                                    uncheckedBorderColor = MaterialTheme.colorScheme.surface,
                                    uncheckedIconColor = MaterialTheme.colorScheme.surface,
                                )
                            )
                        }
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp,  bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "View FAQs",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(start = 16.dp, bottom = 8.dp)
                                    .clickable {
                                        showFAQSheet = true
                                    },
                                color = MaterialTheme.colorScheme.onBackground
                            )

                        }
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp,  bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Change Username",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .clickable {
                                        showUsernameSheet = true
                                    },
                                color = MaterialTheme.colorScheme.onBackground
                            )

                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
//                            Button(
//                                onClick = {
//                                    /// BEGIN SAVE ITEMS
//                                    for(item in _schedule) {
//                                        uid?.let {
//                                            db.collection("users")
//                                                .document(it)
//                                                .update("scheduleItems", FieldValue.arrayUnion(item))
//                                                    .addOnSuccessListener { document ->
//                                                        showBottomSheet = false
//                                                    }
//                                        }
//                                    }
//                                },
//                                modifier = Modifier.padding(top = 32.dp),
//                                shape = RoundedCornerShape(8.dp),
//                                colors = ButtonDefaults.elevatedButtonColors(
//                                    containerColor = MaterialTheme.colorScheme.surface,
//                                    contentColor = MaterialTheme.colorScheme.tertiary
//                                )
//                            ) {
//                                Text("Save Items")
//                            }
//                            Button(
//                                onClick = {
//                                    // BEGIN LOAD ITEMS
//                                    uid?.let {
//                                        db.collection("users")
//                                            .document(it)
//                                            .get()
//                                            .addOnSuccessListener { documentSnapshot ->
//                                                if(documentSnapshot.exists()) {
//                                                    val items = documentSnapshot.get("scheduleItems") as? List<Map<String, Any>>
//                                                    if(items != null) {
//                                                        val entryList = mutableListOf<Entry>()
//                                                        for(itemMap in items) {
//                                                            val entry = mapEntry(itemMap)
//                                                            if(entry !=null) {
//                                                                entryList.add(entry)
//                                                            }
//                                                        }
//                                                        _schedule.addAll(entryList)
//                                                    }
//                                                }
//                                            }
//                                    }
//                                },
//                                modifier = Modifier.padding(top = 32.dp),
//                                shape = RoundedCornerShape(8.dp),
//                                colors = ButtonDefaults.elevatedButtonColors(
//                                    containerColor = MaterialTheme.colorScheme.surface,
//                                    contentColor = MaterialTheme.colorScheme.tertiary
//                                )
//                            ) {
//                                Text("Load Items")
//                            }
//                            Button(
//                                onClick = { showBottomSheet = true },
//                                modifier = Modifier.padding(top = 32.dp),
//                                shape = RoundedCornerShape(8.dp),
//                                colors = ButtonDefaults.elevatedButtonColors(
//                                    containerColor = MaterialTheme.colorScheme.surface,
//                                    contentColor = MaterialTheme.colorScheme.tertiary
//                                )
//                            ) {
//                                Text("Change Username")
//                            }

                            Button(
                                onClick = { authViewModel.signOut() },
                                modifier = Modifier.padding(top = 32.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Text("Sign Out")
                            }
                        }
                    }
                }
            }
        }
    }


    if(showFAQSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showFAQSheet = false
            },
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(.6f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "FAQs",
                            textAlign = TextAlign.Center,
                            fontSize = 26.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.padding(16.dp)
                    )


                    // ANJELLYY FAQssss GOO here

                    FAQListContent()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                showFAQSheet = false
                            },
                            modifier = Modifier.padding(top = 32.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Text("Done")
                        }
                    }
                }
            }
        }
    }


    if(showUsernameSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showUsernameSheet = false
            },
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(.6f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Change Current Username",
                            textAlign = TextAlign.Center,
                            fontSize = 26.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.padding(16.dp)
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.surface
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                            focusedTextColor = MaterialTheme.colorScheme.surface,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.surface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.padding(top = 24.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                uid?.let {
                                    db.collection("users").document(it).update(mapOf("name" to username))
                                        .addOnSuccessListener { document ->
                                            showUsernameSheet = false
                                        }
                                }
                            },
                            modifier = Modifier.padding(top = 32.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}