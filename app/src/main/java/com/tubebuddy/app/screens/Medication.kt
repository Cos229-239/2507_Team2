package com.tubebuddy.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tubebuddy.app.ui.components.BuddyCard
import com.tubebuddy.app.ui.components.EntryType
import com.tubebuddy.app.ui.components.EntryUnits
import com.tubebuddy.app.ui.components.MedType
import com.tubebuddy.app.ui.components.MedicationEntry
import com.tubebuddy.app.ui.components._entryLog
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScreen() {
    //Medicine Variables
    val options = listOf("Zyrtec", "Keppra", "Omeprazole", "Simethicone", "Acetaminophen")
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(options[0]) }

    //Dose Variables
    var doseInput by remember { mutableStateOf("") }

    //Note Variables
    var noteInput by remember { mutableStateOf("") }

    //Time Variables
    var selectedTime: LocalTime? by remember { mutableStateOf(null) }
    var selectedDate: LocalDate by remember { mutableStateOf(LocalDate.now())}
    val finalLocalDateTime: LocalDateTime = remember(selectedTime, selectedDate) {
        val timeToUse = selectedTime ?: LocalTime.of(0, 0) // midnite default if time not selected
        selectedDate.atTime(timeToUse)
    }
    var showTimePicker by remember { mutableStateOf(false) }
    val formattedTime = remember(selectedTime) {
        selectedTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "Select Time"
    }

    //Dialog Variables
    var showDialog by remember { mutableStateOf(false) }
    var errorDialog by remember { mutableStateOf(false)}

    fun clearFields() {
        selectedText = options[0]
        doseInput = ""
        noteInput = ""
        selectedTime = null
    }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.4f)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "Medication Today:",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                ) {
                    BuddyCard("8am", "Zyrtec", "5ml")
                    BuddyCard("10am", "Keppra", "10ml")
                    BuddyCard("1pm", "Omeprazole", "8ml")
                    BuddyCard("3pm", "Simethicone", "1ml")
                    BuddyCard("6pm", "Acetaminophen", "9ml")
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = 8.dp, top = 8.dp),
                        text = "Log Medicine",
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                    )


                    /////////// Medication Selection ///////////
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedText,
                                onValueChange = { }, // Do nothing
                                readOnly = true,
                                label = {
                                    Text(
                                        "Select a Medication",
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onBackground
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.tertiary,
                                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                )
                            )

                            // The actual dropdown menu
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = {
                                    expanded = false
                                },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                // Iterate through options and create menu items
                                options.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                selectionOption,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        },
                                        onClick = {
                                            selectedText =
                                                selectionOption // Update the selected text
                                            expanded = false // Close the dropdown
                                        },
                                        leadingIcon = if (selectionOption == selectedText) {
                                            { Icon(Icons.Default.Check, contentDescription = null) }
                                        } else null
                                    )
                                }
                            }
                        }
                    }


                    /////////// Dose Input ///////////
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        label = { Text(
                            text = "mL",
                            color = MaterialTheme.colorScheme.onBackground) },
                        value = doseInput,
                        onValueChange = { newText ->
                            doseInput = newText
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.tertiary,
                            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )


                    /////////// Notes Input ///////////
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        label = { Text(
                            text = "Notes (optional)",
                            color = MaterialTheme.colorScheme.onBackground) },
                        value = noteInput,
                        onValueChange = { newText ->
                            noteInput = newText
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.tertiary,
                            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                        )
                    )

                    /////////// Time Input ///////////
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 16.dp, end = 16.dp)
                            .clickable(
                                indication = null, // <--- Add this: Removes the ripple effect
                                interactionSource = remember { MutableInteractionSource() },
                            ) {
                                showTimePicker = true
                            }
                    ) {
                        OutlinedTextField(
                            value = formattedTime,
                            onValueChange = { },
                            readOnly = true,
                            enabled = false,
                            label = {
                                Text(
                                    text = "Time",
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            interactionSource = remember { MutableInteractionSource() },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = MaterialTheme.colorScheme.tertiary,
                                disabledContainerColor = MaterialTheme.colorScheme.background,
                                disabledLabelColor = MaterialTheme.colorScheme.onBackground,
                                disabledTextColor = MaterialTheme.colorScheme.onBackground,
                            )
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    /////////// Submit Button ///////////
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ElevatedButton(
                            onClick = {
                                if(doseInput.isBlank()) {
                                    errorDialog = true
                                }
                                else {
                                    _entryLog.add(MedicationEntry(
                                        _type = EntryType.MEDICINE,
                                        _complete = true,
                                        _repeats = false,
                                        _title = selectedText,
                                        _time = finalLocalDateTime,
                                        _amount = doseInput.toDouble(),
                                        _unit = EntryUnits.mL,
                                        _notes = noteInput,
                                        _medType = MedType.ORAL,
                                        _medicationName = selectedText
                                    ))
                                    showDialog = true
                                }
                            },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.onBackground,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                            ) {
                            Text("Submit Medication Log")
                        }
                    }
                }
            }
        }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            containerColor = MaterialTheme.colorScheme.secondary,
            title = { Text(
                text = "Log Entered!",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center) },
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            text = { Text(
                text = "This entry can be found in the\nLog page.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center) },
            textContentColor = MaterialTheme.colorScheme.onBackground,
            confirmButton = {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = {
                        // Dismiss the dialog when the "OK" button is clicked
                        showDialog = false
                        clearFields()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    if(errorDialog) {
        AlertDialog(
            onDismissRequest = {
                errorDialog = false
            },
            containerColor = MaterialTheme.colorScheme.secondary,
            title = { Text(
                text = "Missing Information",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center) },
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            text = { Text(
                text = "Please make sure all required\ninformation is filled out.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center) },
            textContentColor = MaterialTheme.colorScheme.onBackground,
            confirmButton = {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = {
                        // Dismiss the dialog when the "OK" button is clicked
                        errorDialog = false
                        // You can also add other actions here if needed
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
    if(showTimePicker) {
        val currentTime = Calendar.getInstance()
        val initialHour = selectedTime?.hour ?: currentTime.get(Calendar.HOUR_OF_DAY)
        val initialMinute = selectedTime?.minute ?: currentTime.get(Calendar.MINUTE)

        val timePickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = false
        )

        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = {
                        selectedTime = LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTimePicker = false},
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Text("Cancel")
                }
            }
        ) {
            TimeInput(state = timePickerState)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: @Composable () -> Unit = { Text("Select Time") }, // Default title
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit // This is where you pass TimePicker or TimeInput
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        text = content, // The actual time picker goes here
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        containerColor = MaterialTheme.colorScheme.surface,
        textContentColor = MaterialTheme.colorScheme.onPrimary
    )
}

