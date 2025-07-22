package com.example.tubebuddy.screens

import android.icu.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubebuddy.ui.components.Entry
import com.example.tubebuddy.ui.components.EntryType
import com.example.tubebuddy.ui.components.EntryUnits
import com.example.tubebuddy.ui.components.FeedEntry
import com.example.tubebuddy.ui.components.FeedType
import com.example.tubebuddy.ui.components.FlushEntry
import com.example.tubebuddy.ui.components.MedType
import com.example.tubebuddy.ui.components.MedicationEntry
import com.example.tubebuddy.ui.components._entryLog
import com.example.tubebuddy.ui.components._schedule
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.roundToInt

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
                containerColor = Color(186,26,26),
                contentColor = Color.White,
            ), modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .height(50.dp)
                .shadow(5.dp, shape = RoundedCornerShape(8.dp)), shape = RoundedCornerShape(8.dp)) {
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

    //sheet
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    //Item Category Segmented Button
    var newItemCategoriesSelectedIndex by remember { mutableStateOf(0) }
    val newItemCategories = listOf("Feed", "Flush", "Medication")

    //Feed Type Segmented Button
    var newFeedSelectedIndex by remember { mutableStateOf(0) }
    val newFeedCategories = listOf("Bolus", "Gravity", "Pump", "Oral")

    //Medication Dropdown
    var medDDExpanded by remember { mutableStateOf(false) }
    val medicationOptions = listOf("Advil","Tylenol","Aspirin")
    var selectedMedication by remember { mutableStateOf("") }

    //Other New Entry Sheet Fields
    val currentTime = Calendar.getInstance()
    var newLogName by remember { mutableStateOf("") }
    var newNotes by remember { mutableStateOf("") }
    var repeatSwitchOn by remember { mutableStateOf(false) }
    var amountSliderValue by remember { mutableStateOf(50.0f) }
    var medAmountSliderValue by remember { mutableStateOf(5.0f) }

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = false,
    )

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


        //Add new Schedule Item Button
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            onClick = { showBottomSheet = true },
        ) {
            Text(text = "+", fontSize = 24.sp)
        }

        //sheet if item is tapped
        if (logTappedCard != null) {
            ModalBottomSheet(
                onDismissRequest = { logTappedCard = null },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
            ) {
                LogEntryDetailSheet(
                    logTappedCard!!,
                    onDelete = {
                        _entryLog.remove(logTappedCard)
                        logTappedCard = null;
                    },
                    onDismiss = {
                        logTappedCard = null
                    })
            }
        }

        //--------------------Begin New Item Sheet--------------------
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                // Sheet content
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    //-----------Title
                    Text(
                        text = "New Log Entry",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    //-----------Entry Category Segmented Button
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth(.9f)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        newItemCategories.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = newItemCategories.size
                                ),
                                onClick = { newItemCategoriesSelectedIndex = index },
                                selected = index == newItemCategoriesSelectedIndex,
                                label = { Text(label) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))


                    //-----------Feed Type Segmented Button
                    if (newItemCategoriesSelectedIndex == 0){
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier
                                .fillMaxWidth(.9f)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            newFeedCategories.forEachIndexed { index, label ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = newFeedCategories.size
                                    ),
                                    onClick = { newFeedSelectedIndex = index },
                                    selected = index == newFeedSelectedIndex,
                                    label = { Text(label) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    //-----------Medication Dropdown

                    if (newItemCategoriesSelectedIndex == 2){

                        ExposedDropdownMenuBox(
                            expanded = medDDExpanded,
                            onExpandedChange = {medDDExpanded = it}
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = selectedMedication,
                                onValueChange = {},
                                label = {Text("Select a Medication")},
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = medDDExpanded)},
                                modifier = Modifier.menuAnchor(),
                                colors = TextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.DarkGray)
                            )
                            ExposedDropdownMenu(
                                expanded = medDDExpanded,
                                onDismissRequest = {medDDExpanded = false}
                            ) {
                                medicationOptions.forEach{
                                        options->
                                    DropdownMenuItem(
                                        text = {Text(options, color = Color.Black)},
                                        onClick = {
                                            selectedMedication = options;
                                            medDDExpanded = false;
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (newItemCategoriesSelectedIndex == 0 || newItemCategoriesSelectedIndex == 1) {

                        //-----------mL Amount Slider
                        Slider(
                            value = amountSliderValue,
                            onValueChange = { amountSliderValue = it },
                            valueRange = 0f..100f,
                            steps = 99
                        )
                        Text(text = amountSliderValue.roundToInt().toString() + " mL", color = Color.Black)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    else {

                        //-----------mg (medication) Amount Slider
                        Slider(
                            value = medAmountSliderValue,
                            onValueChange = { medAmountSliderValue = it },
                            valueRange = 0f..10f,
                            steps = 9
                        )
                        Text(text = medAmountSliderValue.roundToInt().toString() + " mg", color = Color.Black)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    //-----------User Title Field
                    OutlinedTextField(
                        value = newLogName,
                        onValueChange = { newLogName = it },
                        label = { Text("Enter Title") },
                        colors = TextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.DarkGray)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    //-----------Time Selector
                    TimeInput(
                        state = timePickerState
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    //-----------Repeat Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Repeat   ", // Label text
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Switch(
                            checked = repeatSwitchOn,
                            onCheckedChange = { repeatSwitchOn = it }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    //-----------Notes
                    OutlinedTextField(
                        value = newNotes,
                        onValueChange = { newNotes = it },
                        label = { Text("Notes") },
                        colors = TextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.DarkGray),
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    //-----------Add Item Button
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {

                        Button(
                            onClick = {

                                if (newItemCategoriesSelectedIndex == 0) {
                                    //feed entry
                                    val selectedFeedType = if (newFeedSelectedIndex == 0) {
                                        FeedType.BOLUS
                                    } else if (newFeedSelectedIndex == 1) {
                                        FeedType.GRAVITY
                                    } else if (newFeedSelectedIndex == 2) {
                                        FeedType.PUMP
                                    } else {
                                        FeedType.ORAL
                                    }

                                    _entryLog.add(
                                        FeedEntry(
                                            EntryType.FEED,
                                            false,
                                            repeatSwitchOn,
                                            newLogName,
                                            LocalDateTime.of(
                                                LocalDate.now().year,
                                                LocalDate.now().month,
                                                LocalDate.now().dayOfMonth,
                                                timePickerState.hour,
                                                timePickerState.minute
                                            ),
                                            amountSliderValue.toDouble(),
                                            EntryUnits.mL,
                                            newNotes
                                        )
                                    )
                                } else if (newItemCategoriesSelectedIndex == 1) {
                                    //flush
                                    _entryLog.add(
                                        FlushEntry(
                                            EntryType.FLUSH,
                                            false,
                                            repeatSwitchOn,
                                            newLogName,
                                            LocalDateTime.of(
                                                LocalDate.now().year,
                                                LocalDate.now().month,
                                                LocalDate.now().dayOfMonth,
                                                timePickerState.hour,
                                                timePickerState.minute
                                            ),
                                            amountSliderValue.toDouble(),
                                            EntryUnits.mL,
                                            newNotes
                                        )
                                    )
                                } else if (newItemCategoriesSelectedIndex == 2) {
                                    //medication
                                    _entryLog.add(
                                        MedicationEntry(
                                            EntryType.MEDICINE,
                                            false,
                                            repeatSwitchOn,
                                            newLogName,
                                            LocalDateTime.of(
                                                LocalDate.now().year,
                                                LocalDate.now().month,
                                                LocalDate.now().dayOfMonth,
                                                timePickerState.hour,
                                                timePickerState.minute
                                            ),
                                            medAmountSliderValue.toDouble(),
                                            EntryUnits.mg,
                                            newNotes,
                                            MedType.ORAL,
                                            selectedMedication
                                        )
                                    )
                                }

                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }

                            }, colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color(81, 130, 66),
                                contentColor = Color.White,
                            ),
                            modifier = Modifier.weight(0.75f)
                                .padding(16.dp)
                                .height(50.dp)
                                .shadow(5.dp, shape = RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add To Log"
                            )
                            Text("  Add To Log")
                        }

                        //-----------Cancel Button
                        Button(
                            onClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }
                            }, colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color(186, 26, 26),
                                contentColor = Color.White,
                            ),
                            modifier = Modifier.weight(0.25f)
                                .padding(16.dp)
                                .height(50.dp)
                                .shadow(5.dp, shape = RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Cancel"
                            )
                            Text("Cancel")
                        }
                    }
                }
            }
        }
        //--------------------End New Item Sheet--------------------
    }
}