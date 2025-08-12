package com.tubebuddy.app.screens

import android.icu.util.Calendar
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tubebuddy.app.ui.components.Entry
import com.tubebuddy.app.ui.components.EntryType
import com.tubebuddy.app.ui.components.EntryUnits
import com.tubebuddy.app.ui.components.FeedEntry
import com.tubebuddy.app.ui.components.FeedType
import com.tubebuddy.app.ui.components.FilterType
import com.tubebuddy.app.ui.components.FlushEntry
import com.tubebuddy.app.ui.components.MedType
import com.tubebuddy.app.ui.components.MedicationEntry
import com.tubebuddy.app.ui.components._currFilter
import com.tubebuddy.app.ui.components._entryLog
import kotlinx.coroutines.launch
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.roundToInt

enum class MonthList{
    Jan, Feb, Mar, April, May, June, July, Aug, Sep, Oct, Nov, Dec
}

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

                    Text(text = logFormatTime(entry._time),
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("Title: ${entry._title}", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Medium)

            Row {
                Text("Type: ${entry._type}", color = MaterialTheme.colorScheme.onPrimary)

                if (entry is FeedEntry)
                    Text(" • ${entry._feedType}", color = MaterialTheme.colorScheme.onPrimary)
            }

            Text("Amount: ${entry._amount} ${entry._unit}", color = MaterialTheme.colorScheme.onPrimary)
            Text("Notes: ${entry._notes}", color = MaterialTheme.colorScheme.onPrimary)

            if (entry is MedicationEntry) {
                Text("Medication: (" + "${entry._medType}" + ") ${entry._medicationName}", color = MaterialTheme.colorScheme.onPrimary)
                //Text("Med Type: ${entry._medType}", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(10.dp))

        }

        Button(
            onClick = onDelete,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
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
    val logContext = LocalContext.current

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

    //Date Entry
    var selectedMonth by remember { mutableStateOf(MonthList.entries[LocalDate.now().monthValue - 1]) }
    var dayString by remember { mutableStateOf(LocalDate.now().dayOfMonth.toString()) }
    var yearString by remember { mutableStateOf(LocalDate.now().year.toString()) }
    var monthDDExpanded by remember { mutableStateOf(false) }

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
            Text(text = "No Logged Items", fontSize = 30.sp, color = MaterialTheme.colorScheme.onPrimary)
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //displays each entry in log
                items(_entryLog) {
                    //filter
                    entry -> if (_currFilter.value!= FilterType.ALL_FILTER){
                        if (_currFilter.value==FilterType.FEED_FILTER){
                            if (entry._type != EntryType.FEED)
                                return@items
                        }
                        if (_currFilter.value==FilterType.FLUSH_FILTER){
                            if (entry._type != EntryType.FLUSH)
                                return@items
                        }
                        if (_currFilter.value==FilterType.MEDICINE_FILTER){
                            if (entry._type != EntryType.MEDICINE)
                                return@items
                        }
                    }

                    LogBuddyCard(
                        entry,
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
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.tertiary
        ) {
            Text(text = "+", fontSize = 24.sp)
        }

        //sheet if item is tapped
        if (logTappedCard != null) {
            ModalBottomSheet(
                onDismissRequest = { logTappedCard = null },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                LogEntryDetailSheet(
                    logTappedCard!!,
                    onDelete = {
                        _entryLog.remove(logTappedCard)
                        logTappedCard = null
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
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                // Sheet content
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //-----------Title
                    Text(
                        text = "New Log Entry",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

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
                                label = { Text(label) },
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = MaterialTheme.colorScheme.tertiary,
                                    activeContentColor = MaterialTheme.colorScheme.onTertiary
                                )
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
                                    label = { Text(label) },
                                    colors = SegmentedButtonDefaults.colors(
                                        activeContainerColor = MaterialTheme.colorScheme.tertiary,
                                        activeContentColor = MaterialTheme.colorScheme.onTertiary
                                    )
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
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                                    focusedTextColor = MaterialTheme.colorScheme.surface,
                                    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.surface,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.surface)
                            )
                            ExposedDropdownMenu(
                                expanded = medDDExpanded,
                                onDismissRequest = {medDDExpanded = false}
                            ) {
                                medicationOptions.forEach{
                                        options->
                                    DropdownMenuItem(
                                        text = {Text(options, color = MaterialTheme.colorScheme.surface)},
                                        onClick = {
                                            selectedMedication = options
                                            medDDExpanded = false
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
                            onValueChange = { amountSliderValue = it.roundToInt().toFloat() },
                            valueRange = 0f..100f,
                            steps = 0,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.tertiary,
                                activeTrackColor = MaterialTheme.colorScheme.tertiary,
                                activeTickColor = MaterialTheme.colorScheme.tertiary,
                                inactiveTrackColor = MaterialTheme.colorScheme.onTertiary,
                                inactiveTickColor = MaterialTheme.colorScheme.tertiary,
                            )
                        )
                        Text(text = amountSliderValue.roundToInt().toString() + " mL", color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    else {

                        //-----------mg (medication) Amount Slider
                        Slider(
                            value = medAmountSliderValue,
                            onValueChange = { medAmountSliderValue = it.roundToInt().toFloat() },
                            valueRange = 0f..10f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.tertiary,
                                activeTrackColor = MaterialTheme.colorScheme.tertiary,
                                activeTickColor = MaterialTheme.colorScheme.tertiary,
                                inactiveTrackColor = MaterialTheme.colorScheme.onTertiary,
                                inactiveTickColor = MaterialTheme.colorScheme.tertiary,
                            )
                        )
                        Text(text = medAmountSliderValue.roundToInt().toString() + " mg", color = Color.Black)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    //-----------User Title Field
                    OutlinedTextField(
                        value = newLogName,
                        onValueChange = { newLogName = it },
                        label = { Text("Enter Title") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                            focusedTextColor = MaterialTheme.colorScheme.surface,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.surface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.surface)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    //-----------Date Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(.85f)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //-----------Month Dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            ExposedDropdownMenuBox(
                                expanded = monthDDExpanded,
                                onExpandedChange = { monthDDExpanded = !monthDDExpanded }
                            ) {
                                OutlinedTextField(
                                    readOnly = true,
                                    value = selectedMonth.name,
                                    onValueChange = {},
                                    label = { Text("Month") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthDDExpanded)
                                    },
                                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                    ,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                                        focusedTextColor = MaterialTheme.colorScheme.surface,
                                        focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.surface,
                                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface)
                                )
                                ExposedDropdownMenu(
                                    expanded = monthDDExpanded,
                                    onDismissRequest = { monthDDExpanded = false }
                                ) {
                                    MonthList.entries.forEach { month ->
                                        DropdownMenuItem(
                                            text = { Text(month.name, color = MaterialTheme.colorScheme.surface) },
                                            onClick = {
                                                selectedMonth = month
                                                monthDDExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Day TextField
                        OutlinedTextField(
                            value = dayString,
                            onValueChange = { if (it.length <= 2) dayString = it },
                            label = { Text("Day") },
                            modifier = Modifier.weight(0.6f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                                focusedTextColor = MaterialTheme.colorScheme.surface,
                                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.surface,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Year TextField
                        OutlinedTextField(
                            value = yearString,
                            onValueChange = { if (it.length <= 4) yearString = it },
                            label = { Text("Year") },
                            modifier = Modifier.weight(.75f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                                focusedTextColor = MaterialTheme.colorScheme.surface,
                                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.surface,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    //-----------Time Selector
                    TimeInput(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.tertiary,
                            timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.onSurface,
                            timeSelectorSelectedContentColor = MaterialTheme.colorScheme.surface,
                            timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.surface,
                            periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.tertiary,
                            periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.onSurface,
                            periodSelectorSelectedContentColor = MaterialTheme.colorScheme.surface,
                            periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.surface,
                        )
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
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Switch(
                            checked = repeatSwitchOn,
                            onCheckedChange = { repeatSwitchOn = it },
                            colors = SwitchColors(
                                checkedThumbColor = MaterialTheme.colorScheme.tertiary,
                                checkedTrackColor = MaterialTheme.colorScheme.surface,
                                checkedBorderColor = MaterialTheme.colorScheme.tertiary,
                                checkedIconColor = MaterialTheme.colorScheme.tertiary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                                uncheckedTrackColor = MaterialTheme.colorScheme. onSurface,
                                uncheckedBorderColor = MaterialTheme.colorScheme.surface,
                                uncheckedIconColor = MaterialTheme.colorScheme.surface,
                                disabledCheckedThumbColor = MaterialTheme.colorScheme.tertiary,
                                disabledCheckedTrackColor = MaterialTheme.colorScheme.tertiary,
                                disabledCheckedBorderColor = MaterialTheme.colorScheme.tertiary,
                                disabledCheckedIconColor = MaterialTheme.colorScheme.tertiary,
                                disabledUncheckedThumbColor = MaterialTheme.colorScheme.tertiary,
                                disabledUncheckedTrackColor = MaterialTheme.colorScheme.tertiary,
                                disabledUncheckedBorderColor = MaterialTheme.colorScheme.tertiary,
                                disabledUncheckedIconColor =MaterialTheme.colorScheme.tertiary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    //-----------Notes
                    OutlinedTextField(
                        value = newNotes,
                        onValueChange = { newNotes = it },
                        label = { Text("Notes") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                            focusedTextColor = MaterialTheme.colorScheme.surface,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.surface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.surface),
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

                                if (newLogName.isEmpty()){
                                    Toast.makeText(logContext, "Please enter a title.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (!isValidDate(yearString, selectedMonth, dayString)){
                                    Toast.makeText(logContext, "Please enter a valid date.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (newItemCategoriesSelectedIndex == 2 && selectedMedication.isBlank()){
                                    Toast.makeText(logContext, "Please select a medication.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

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

                                    insertEntry(
                                        FeedEntry(
                                            EntryType.FEED,
                                            false,
                                            repeatSwitchOn,
                                            newLogName,
                                            LocalDateTime.of(
                                                yearString.toInt(),
                                                (selectedMonth.ordinal + 1),
                                                dayString.toInt(),
                                                timePickerState.hour,
                                                timePickerState.minute
                                            ),
                                            amountSliderValue.toDouble(),
                                            EntryUnits.mL,
                                            newNotes,
                                            selectedFeedType
                                        )
                                    )
                                } else if (newItemCategoriesSelectedIndex == 1) {
                                    //flush
                                    insertEntry(
                                        FlushEntry(
                                            EntryType.FLUSH,
                                            false,
                                            repeatSwitchOn,
                                            newLogName,
                                            LocalDateTime.of(
                                                yearString.toInt(),
                                                (selectedMonth.ordinal + 1),
                                                dayString.toInt(),
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
                                    insertEntry(
                                        MedicationEntry(
                                            EntryType.MEDICINE,
                                            false,
                                            repeatSwitchOn,
                                            newLogName,
                                            LocalDateTime.of(
                                                yearString.toInt(),
                                                (selectedMonth.ordinal + 1),
                                                dayString.toInt(),
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

                            }, colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = Color.White,
                            ),
                            modifier = Modifier.weight(0.75f)
                                .padding(start = 16.dp)
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
                            }, colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = Color.White,
                            ),
                            modifier = Modifier.weight(0.25f)
                                .padding(start = 16.dp, end = 16.dp)
                                .height(50.dp)
                                .shadow(5.dp, shape = RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Cancel"
                            )
                            //Text("Cancel")
                        }
                    }
                }
            }
        }
        //--------------------End New Item Sheet--------------------
    }
}

fun insertEntry(entry: Entry){
    //find index to insert
    val insertIndex = _entryLog.indexOfFirst { it._time.isAfter(entry._time) }

    if (insertIndex < 0){
        _entryLog.add(entry)
    }
    else{
        _entryLog.add(insertIndex, entry)
    }
}

//Generate Log Buddy Cards
@Composable
fun LogBuddyCard(entry: Entry, modifier: Modifier = Modifier) {

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
                    containerColor = MaterialTheme.colorScheme.primary),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    Text(
                        text = logFormatDate(entry._time),
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
                        text = logFormatTime(entry._time) + " • " + entry._type.toString(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    if (entry is FeedEntry) {
                        Text(
                            text = " • " + entry._feedType.toString(),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondary,
                        )
                    }
                }
            }
        }
    }
}

fun logFormatDate(dateTime: LocalDateTime): String {
    return (dateTime.monthValue.toString() + '/' + dateTime.dayOfMonth.toString())
}

fun logFormatTime(dateTime: LocalDateTime): String {
    val minute = dateTime.minute.toString().padStart(2, '0')
    return when {
        dateTime.hour == 0 -> "12:$minute AM"
        dateTime.hour == 12 -> "12:$minute PM"
        dateTime.hour > 12 -> "${dateTime.hour - 12}:$minute PM"
        else -> "${dateTime.hour}:$minute AM"
    }
}

fun isValidDate(_year: String, _month: MonthList, _day: String) : Boolean{
    return try {
        LocalDate.of(_year.toInt(), (_month.ordinal + 1), _day.toInt())
        true
    } catch (_: DateTimeException){
        false
    }
}