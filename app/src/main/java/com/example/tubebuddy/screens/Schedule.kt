package com.example.tubebuddy.screens

import android.graphics.Paint.Align
import android.icu.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tubebuddy.ui.components.EntryType
import com.example.tubebuddy.ui.components.EntryUnits
import com.example.tubebuddy.ui.components.FeedEntry
import com.example.tubebuddy.ui.components.FeedType
import com.example.tubebuddy.ui.components.FlushEntry
import com.example.tubebuddy.ui.components.MedType
import com.example.tubebuddy.ui.components.MedicationEntry
import com.example.tubebuddy.ui.components._schedule
import com.example.tubebuddy.ui.components.Entry
import com.example.tubebuddy.ui.components._entryLog
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.roundToInt

//Schedule Detail Sheet
@Composable
fun EntryDetailSheet(entry: Entry, onDelete:()->Unit, onDismiss:()->Unit) {

    val actualAmountSliderValue = remember(entry) { mutableStateOf(entry._amount.toFloat()) }
    val medActualAmountSliderValue = remember(entry) { mutableStateOf(entry._amount.toFloat()) }
    var timeString by remember { mutableStateOf("") }

    if (entry._time.hour == 0){
        timeString = "12:" + entry._time.minute.toString() + " AM"
    }
    else if (entry._time.hour > 12){
        timeString = (entry._time.hour-12).toString() + ":" + entry._time.minute.toString() + " PM"
    }
    else if (entry._time.hour == 12){
        timeString = "12:" + entry._time.minute.toString() + " PM"
    }
    else{
        timeString = entry._time.hour.toString() + ":" + entry._time.minute.toString() + " AM"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.45f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card (modifier = Modifier
                .size(100.dp, 80.dp), colors = CardDefaults.cardColors(containerColor = Color(31,46,68)), elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)){
                Column (
                    modifier = Modifier
                        .fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text = entry._time.monthValue.toString() + '/' + entry._time.dayOfMonth,
                        fontSize = 25.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(text = timeString,
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("Title: ${entry._title}", color = Color.Black, fontWeight = FontWeight.Medium)
            Text("Type: ${entry._type}", color = Color.Black)
            Text("Amount: ${entry._amount} ${entry._unit}", color = Color.Black)
            Text("Notes: ${entry._notes}", color = Color.Black)

            if (entry is MedicationEntry) {
                Text("Medication: (" + "${entry._medType}" + ") ${entry._medicationName}", color = Color.Black)
                //Text("Med Type: ${entry._medType}", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (entry._type == EntryType.MEDICINE){

                Slider(
                    value = medActualAmountSliderValue.value,
                    onValueChange = { medActualAmountSliderValue.value = it },
                    valueRange = 0f..10f,
                    steps = 9
                )
                Text(text = medActualAmountSliderValue.value.roundToInt().toString() + " mg", color = Color.Black)
            }
            else{

                Slider(
                    value = actualAmountSliderValue.value,
                    onValueChange = { actualAmountSliderValue.value = it },
                    valueRange = 0f..100f,
                    steps = 99
                )
                Text(text = actualAmountSliderValue.value.roundToInt().toString() + " mL", color = Color.Black)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ){
            Button(
                onClick = {
                    if (entry is FeedEntry || entry is FlushEntry)
                        _entryLog.add(FeedEntry(entry._type, _complete = true, _repeats = false, entry._title, entry._time, actualAmountSliderValue.value.toDouble(), entry._unit, entry._notes))
                    if (entry is MedicationEntry)
                        _entryLog.add(MedicationEntry(entry._type, _complete = true, _repeats = false, entry._title, entry._time, medActualAmountSliderValue.value.toDouble(), entry._unit, entry._notes, entry._medType, entry._medicationName))

                    onDismiss()
                },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(81,130,66),
                    contentColor = Color.White,
                ), modifier = Modifier.weight(0.75f)
                    .padding(16.dp)
                    .height(50.dp)
                    .shadow(5.dp, shape = RoundedCornerShape(8.dp)), shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "  Quick Add to Log"
                )
                Text("Quick Add")
            }

            Button(
                onClick = onDelete,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(186,26,26),
                    contentColor = Color.White,
            ), modifier = Modifier.weight(0.25f)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ScheduleScreen() {

    val context = LocalContext.current

    //sheet
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    //info sheet
    var tappedCard by remember { mutableStateOf<Entry?>(null) }

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

    //Main Schedule Screen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        //if log is empty display basic text
        if (!(_schedule.size >= 1))
            Text(text = "No Scheduled Items", fontSize = 30.sp, color = Color.Black)
        else{
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //displays each entry in log
                items(_schedule) {
                    entry->ScheduleBuddyCard(entry._time.hour.toString(), entry._title, entry._type.toString(), modifier = Modifier
                    .size(width = 380.dp, height = 94.dp)
                    .padding(bottom = 18.dp)
                    .clickable { tappedCard = entry })
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
        if (tappedCard!=null){
            ModalBottomSheet(
                onDismissRequest = { tappedCard = null },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
            ) {
                EntryDetailSheet(tappedCard!!,
                    onDelete = {
                        _schedule.remove(tappedCard)
                        tappedCard = null;
                    },
                    onDismiss = {
                        tappedCard = null
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
                        text = "Create New Event",
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

                            _schedule.add(FeedEntry(
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
                            _schedule.add(FlushEntry(EntryType.FLUSH, false, repeatSwitchOn, newLogName, LocalDateTime.of(
                                LocalDate.now().year,LocalDate.now().month,LocalDate.now().dayOfMonth,timePickerState.hour,timePickerState.minute) , amountSliderValue.toDouble(), EntryUnits.mL, newNotes))
                        }
                        else if (newItemCategoriesSelectedIndex == 2){
                            //medication
                            _schedule.add(MedicationEntry(EntryType.MEDICINE, false, repeatSwitchOn, newLogName, LocalDateTime.of(
                                LocalDate.now().year,LocalDate.now().month,LocalDate.now().dayOfMonth,timePickerState.hour,timePickerState.minute) , medAmountSliderValue.toDouble(), EntryUnits.mg, newNotes, MedType.ORAL, selectedMedication))
                        }

                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }

                    }, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White)) {
                        Text("Add To List")
                    }

                    //-----------Cancel Button
                    Button(onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.White)) {
                        Text("Cancel")
                    }
                }
            }
        }
        //--------------------End New Item Sheet--------------------
    }
}

//Generate Schedule Buddy Cards
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
                    containerColor = MaterialTheme.colorScheme.onBackground
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