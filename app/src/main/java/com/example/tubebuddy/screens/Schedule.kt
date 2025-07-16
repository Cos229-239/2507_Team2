package com.example.tubebuddy.screens

import android.icu.util.Calendar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubebuddy.ui.components.BuddyCard
import com.example.tubebuddy.ui.components.EntryType
import com.example.tubebuddy.ui.components.EntryUnits
import com.example.tubebuddy.ui.components.FeedEntry
import com.example.tubebuddy.ui.components.FeedType
import com.example.tubebuddy.ui.components.FlushEntry
import com.example.tubebuddy.ui.components.MedType
import com.example.tubebuddy.ui.components.MedicationEntry
import com.example.tubebuddy.ui.components._log
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ScheduleScreen() {

    val context = LocalContext.current

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

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = false,
    )

    //Main Schedule Screen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        //if log is empty display basic text
        if (!(_log.size >= 1))
            Text(text = "Schedule Screen", fontSize = 30.sp)
        else{
            Column {
                //displays each entry in log
                for (Entry in _log){
                    BuddyCard(Entry._time.hour.toString(), Entry._title, Entry._type.toString())
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
                        text = "Create New Event",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    //-----------Entry Category Segmented Button
                    SingleChoiceSegmentedButtonRow {
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
                        SingleChoiceSegmentedButtonRow {
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
                                trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = medDDExpanded)},
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

                    //-----------mL Amount Slider
                    Slider(
                        value = amountSliderValue,
                        onValueChange = {amountSliderValue = it},
                        valueRange = 0f..100f
                    )
                    Text(text = amountSliderValue.toString() + " mL", color = Color.Black)
                    Spacer(modifier = Modifier.height(10.dp))

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
                            //.padding(10.dp)
                            //.height(50.dp)

                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    //-----------Add Item Button
                    Button(onClick = {

                        if (newItemCategoriesSelectedIndex == 0){
                            //feed entry
                            val selectedFeedType = if (newFeedSelectedIndex == 0){
                                FeedType.BOLUS
                            }
                            else if (newFeedSelectedIndex == 1){
                                FeedType.GRAVITY
                            }
                            else if (newFeedSelectedIndex == 2){
                                FeedType.PUMP
                            }
                            else {
                                FeedType.ORAL
                            }

                            _log.add(FeedEntry(
                                EntryType.FEED,
                                false,
                                repeatSwitchOn,
                                newLogName,
                                LocalDateTime.of(
                                    LocalDate.now().year,LocalDate.now().month,LocalDate.now().dayOfMonth,timePickerState.hour,timePickerState.minute),
                                amountSliderValue.toDouble(),
                                EntryUnits.mL,
                                newNotes
                            ))
                        }
                        else if (newItemCategoriesSelectedIndex == 1){
                            //flush
                            _log.add(FlushEntry(EntryType.FLUSH, false, repeatSwitchOn, newLogName, LocalDateTime.of(
                                LocalDate.now().year,LocalDate.now().month,LocalDate.now().dayOfMonth,timePickerState.hour,timePickerState.minute) , amountSliderValue.toDouble(), EntryUnits.mL, newNotes))
                        }
                        else if (newItemCategoriesSelectedIndex == 2){
                            //medication
                            _log.add(MedicationEntry(EntryType.MEDICINE, false, repeatSwitchOn, newLogName, LocalDateTime.of(
                                LocalDate.now().year,LocalDate.now().month,LocalDate.now().dayOfMonth,timePickerState.hour,timePickerState.minute) , amountSliderValue.toDouble(), EntryUnits.mg, newNotes, MedType.ORAL, selectedMedication))
                        }

                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }

                    }) {
                        Text("Add To List")
                    }

                    //-----------Cancel Button
                    Button(onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }) {
                        Text("Cancel")
                    }
                }
            }
        }
        //--------------------End New Item Sheet--------------------
    }
}