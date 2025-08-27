package com.tubebuddy.app.screens

import android.icu.util.Calendar
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
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import android.widget.Toast
import androidx.compose.material.icons.filled.Cached
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.tubebuddy.app.ui.components.Entry
import com.tubebuddy.app.ui.components._currFilter
import com.tubebuddy.app.ui.components.EntryType
import com.tubebuddy.app.ui.components.EntryUnits
import com.tubebuddy.app.ui.components.FeedEntry
import com.tubebuddy.app.ui.components.FilterType
import com.tubebuddy.app.ui.components.FeedType
import com.tubebuddy.app.ui.components.FlushEntry
import com.tubebuddy.app.ui.components.MedEntry
import com.tubebuddy.app.ui.components.MedType
import com.tubebuddy.app.ui.components.MedicationEntry
import com.tubebuddy.app.ui.components._entryLog
import com.tubebuddy.app.ui.components._medLog
import com.tubebuddy.app.ui.components._schedule
import com.tubebuddy.app.ui.components.deleteScheduleItemFB
import com.tubebuddy.app.ui.components.entryToFirestoreMap
import com.tubebuddy.app.ui.components.isNewDay
import com.tubebuddy.app.ui.components.itemCheckedMap
import com.tubebuddy.app.ui.components.loadLogItemsFromFB
import com.tubebuddy.app.ui.components.pushScheduleItemToFirestore
import com.tubebuddy.app.ui.components.refreshItemCheckedMapFromSchedule
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

//Schedule Detail Sheet
@Composable
fun EntryDetailSheet(entry: Entry, onDelete:()->Unit, onDismiss:()->Unit) {

    val actualAmountSliderValue = remember(entry) { mutableFloatStateOf(entry._amount.toFloat()) }
    val medActualAmountSliderValue = remember(entry) { mutableFloatStateOf(entry._amount.toFloat()) }

    //context to be used to check if it is a new day
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.5f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(bottom = 10.dp),
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

            Row {
                Text(
                    "Title: ${entry._title}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                if (entry._repeats) {
                    Icon(
                        imageVector = Icons.Default.Cached,
                        contentDescription = "Repeat",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            Row {
                Text("Type: ${entry._type}", color = MaterialTheme.colorScheme.onPrimary)

                if (entry is FeedEntry)
                    Text(" • ${entry._feedType}", color = MaterialTheme.colorScheme.onPrimary)
            }

            Text("Amount: ${entry._amount} ${entry._unit}", color =MaterialTheme.colorScheme.onSurface)
            Text("Notes: ${entry._notes}", color = MaterialTheme.colorScheme.onSurface)

            if (entry is MedicationEntry) {
                Text("Medication: (" + "${entry._medType}" + ") ${entry._medicationName}", color = MaterialTheme.colorScheme.onSurface)
                //Text("Med Type: ${entry._medType}", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (entry._type == EntryType.MEDICINE){

                Slider(
                    value = medActualAmountSliderValue.floatValue,
                    onValueChange = { medActualAmountSliderValue.floatValue = it.roundToInt().toFloat() },
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
                Text(text = medActualAmountSliderValue.floatValue.roundToInt().toString() + " mg", color = MaterialTheme.colorScheme.onSurface)
            }
            else{

                Slider(
                    value = actualAmountSliderValue.floatValue,
                    onValueChange = { actualAmountSliderValue.floatValue = it.roundToInt().toFloat() },
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
                Text(text = actualAmountSliderValue.floatValue.roundToInt().toString() + " mL", color = MaterialTheme.colorScheme.onSurface)
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
                    if (entry is FeedEntry)
                        insertEntry(FeedEntry(entry._type, _complete = true, _repeats = false, entry._title, LocalDateTime.now(), actualAmountSliderValue.floatValue.toDouble(), entry._unit, entry._notes, entry._feedType))
                    if (entry is FlushEntry)
                        insertEntry(FlushEntry(entry._type, _complete = true, _repeats = false, entry._title, LocalDateTime.now(), actualAmountSliderValue.floatValue.toDouble(), entry._unit, entry._notes))
                    if (entry is MedicationEntry)
                        insertEntry(MedicationEntry(entry._type, _complete = true, _repeats = false, entry._title, LocalDateTime.now(), medActualAmountSliderValue.floatValue.toDouble(), entry._unit, entry._notes, entry._medType, entry._medicationName))

                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = Color.White,
                ), modifier = Modifier.weight(0.75f)
                    .padding(start=16.dp)
                    .height(50.dp)
                    .shadow(5.dp, shape = RoundedCornerShape(8.dp)), shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "  Quick Add to Log"
                )
                Text("  Quick Log")
            }

            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = Color.White,
            ), modifier = Modifier.weight(0.25f)
                .padding(start = 16.dp,end = 16.dp)
                .height(50.dp)
                .shadow(5.dp, shape = RoundedCornerShape(8.dp)), shape = RoundedCornerShape(8.dp)) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ScheduleScreen() {

    val schedContext = LocalContext.current

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
    var selectedMedication by remember { mutableStateOf<MedEntry>(object : MedEntry {
        override val _name: String = ""
    }) }

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

    //code that runs each time the schedule screen appears
    //check if its a new day to clear old (non-repeating) schedule items
    LaunchedEffect(Unit) {
        if (_schedule.isEmpty() && _entryLog.isEmpty()) {
            loadItemsFromFB()
        }
        if (isNewDay(schedContext)){
            newDayClearCompleteEntries(schedContext)
        }
    }

    //Main Schedule Screen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val didSanitize = rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(_schedule.size) {
            if (!didSanitize.value && _schedule.isNotEmpty()) {
                dedupeSchedule()                 // remove one-time dupes
                refreshItemCheckedMapFromSchedule()      // mirror to legacy map for UI
                saveFullScheduleToFirestore()            // persist cleaned array
                didSanitize.value = true                 // run only once per app start
            }
        }
        //if log is empty display basic text
        if (_schedule.isEmpty())
            Text(text = "No Scheduled Items", fontSize = 30.sp, color = MaterialTheme.colorScheme.onPrimary)
        else{
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //displays each entry in log
                items(_schedule) {
                    scheduledItem -> val isItChecked = itemCheckedMap.getOrDefault(scheduledItem, scheduledItem._checked)

                    //filter
                    if (_currFilter.value!= FilterType.ALL_FILTER){
                        if (_currFilter.value==FilterType.FEED_FILTER){
                            if (scheduledItem._type != EntryType.FEED)
                                return@items
                        }
                        if (_currFilter.value==FilterType.FLUSH_FILTER){
                            if (scheduledItem._type != EntryType.FLUSH)
                                return@items
                        }
                        if (_currFilter.value==FilterType.MEDICINE_FILTER){
                            if (scheduledItem._type != EntryType.MEDICINE)
                                return@items
                        }
                    }

                    ScheduleBuddyCard(
                        scheduledItem,
                        modifier = Modifier
                            .size(width = 380.dp, height = 94.dp)
                            .padding(bottom = 18.dp)
                            .clickable { tappedCard = scheduledItem },
                        isChecked = isItChecked,
                        onCheckChecked = { isNowChecked ->

                            // 1) Update reactive UI state FIRST so the checkbox flips immediately.
                            itemCheckedMap[scheduledItem] = isNowChecked

                            // 2) Persist on the model (so Firestore gets it too).
                            scheduledItem._checked = isNowChecked

                            // 3) Preserve your existing behavior: when checked, add a log entry at NOW.
                            if (isNowChecked) {
                                when (scheduledItem) {
                                    is FeedEntry -> insertEntryAndFB(
                                        FeedEntry(
                                            _type = scheduledItem._type,
                                            _complete = true,
                                            _repeats = false,
                                            _title = scheduledItem._title,
                                            _time = LocalDateTime.now(),
                                            _amount = scheduledItem._amount,
                                            _unit = scheduledItem._unit,
                                            _notes = scheduledItem._notes,
                                            _feedType = scheduledItem._feedType
                                        )
                                    )
                                    is FlushEntry -> insertEntryAndFB(
                                        FlushEntry(
                                            _type = scheduledItem._type,
                                            _complete = true,
                                            _repeats = false,
                                            _title = scheduledItem._title,
                                            _time = LocalDateTime.now(),
                                            _amount = scheduledItem._amount,
                                            _unit = scheduledItem._unit,
                                            _notes = scheduledItem._notes
                                        )
                                    )
                                    is MedicationEntry -> insertEntryAndFB(
                                        MedicationEntry(
                                            _type = scheduledItem._type,
                                            _complete = true,
                                            _repeats = false,
                                            _title = scheduledItem._title,
                                            _time = LocalDateTime.now(),
                                            _amount = scheduledItem._amount,
                                            _unit = scheduledItem._unit,
                                            _notes = scheduledItem._notes,
                                            _medType = scheduledItem._medType,
                                            _medicationName = scheduledItem._medicationName
                                        )
                                    )
                                }
                            }

                            // 4) Write the ENTIRE schedule array once — this avoids the
                            //    "legacy object without _checked could not be removed" problem
                            //    that caused one-time duplicates after migration.
                            saveFullScheduleToFirestore()

                        }
                    )


                }
            }
        }


        //remove this button, for testing only

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 225.dp),
            onClick = { clearAndPopulateWithStandardItems() },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.tertiary
        ) {
            Text(text = "Populate", fontSize = 24.sp, modifier = Modifier.padding(10.dp))
        }


        //remove this button, for testing new day only

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 95.dp),
            onClick = { newDayClearCompleteEntries(schedContext) },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.tertiary
        ) {
            Text(text = "New Day", fontSize = 24.sp, modifier = Modifier.padding(10.dp))
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
        if (tappedCard!=null){
            ModalBottomSheet(
                onDismissRequest = { tappedCard = null },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                EntryDetailSheet(tappedCard!!,
                    onDelete = {
                        tappedCard?.let { deleteScheduleItemFB(it) }
                        _schedule.remove(tappedCard)
                        tappedCard = null
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
                        text = "New Schedule Entry",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondary,
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
                        Spacer(modifier = Modifier.height(8.dp))


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
                                value = selectedMedication._name,
                                onValueChange = {},
                                label = {Text("Select a Medication")},
                                trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = medDDExpanded)},
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
                                _medLog.forEach{
                                    options->
                                    DropdownMenuItem(
                                        text = { Text(options._name, color = MaterialTheme.colorScheme.surface)},
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
                        Text(text = amountSliderValue.roundToInt().toString() + " mL", color = MaterialTheme.colorScheme.onSecondary)
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
                        Text(
                            text = medAmountSliderValue.roundToInt().toString() + " mg",
                            color = MaterialTheme.colorScheme.onSecondary)

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
                            color = MaterialTheme.colorScheme.onSecondary
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

                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        //-----------Add Item Button
                        Button(
                            onClick = {

                                if (newLogName.isEmpty()){
                                    Toast.makeText(schedContext, "Please enter a title.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (newItemCategoriesSelectedIndex == 2 && selectedMedication._name==""){
                                    Toast.makeText(schedContext, "Please select a medication.", Toast.LENGTH_SHORT).show()
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

                                    insertScheduleEntryAndFB(
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
                                            newNotes,
                                            selectedFeedType
                                        )
                                    )
                                } else if (newItemCategoriesSelectedIndex == 1) {
                                    //flush
                                    insertScheduleEntryAndFB(
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
                                    insertScheduleEntryAndFB(
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
                                            selectedMedication._name
                                        )
                                    )
                                }

                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }

                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
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
                                contentDescription = "Add To Schedule"
                            )
                            Text("  Add To Schedule")
                        }

                        //-----------Cancel Button
                        Button(
                            onClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = Color.White,
                            ),
                            modifier = Modifier.weight(0.25f)
                                .padding(top = 16.dp, end=16.dp)
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

//Generate Schedule Buddy Cards
@Composable
fun ScheduleBuddyCard(entry: Entry,
                      modifier: Modifier = Modifier,
                      isChecked: Boolean,
                      onCheckChecked: (Boolean) -> Unit) {

    Row{

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
                    containerColor = if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary
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

                Row {
                    Text(
                        text = entry._title,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.padding(start = 8.dp),
                        textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                    )

                    if (entry._repeats) {
                        Icon(
                            imageVector = Icons.Default.Cached,
                            contentDescription = "Repeat",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Row {
                    Text(
                        text = entry._type.toString(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    if (entry is FeedEntry) {
                        Text(
                            text = " • " + entry._feedType.toString(),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
                ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = onCheckChecked
                )
            }
        }
    }
    }
}

fun insertScheduleEntry(entry: Entry){
    //find index to insert
    val insertIndex = _schedule.indexOfFirst { it._time.isAfter(entry._time) }

    if (insertIndex < 0){
        _schedule.add(entry)
    }
    else{
        _schedule.add(insertIndex, entry)
    }
}

fun insertScheduleEntryAndFB(entry: Entry){
    //find index to insert
    val insertIndex = _schedule.indexOfFirst { it._time.isAfter(entry._time) }

    if (insertIndex < 0){
        _schedule.add(entry)
    }
    else{
        _schedule.add(insertIndex, entry)
    }

    pushScheduleItemToFirestore(entry)
}

fun scheduleFormatShortTime(dateTime: LocalDateTime): String {
    val minute = dateTime.minute.toString().padStart(2, '0')
    return when {
        dateTime.hour == 0 -> "12a"
        dateTime.hour == 12 -> "12p"
        dateTime.hour > 12 -> "${dateTime.hour - 12}p"
        else -> "${dateTime.hour}a"
    }
}

fun newDayClearCompleteEntries(context: Context) {
    val toRemove = _schedule.filter { it._checked && !it._repeats }
    _schedule.removeAll(toRemove)
    _schedule.filter { it._repeats }.forEach { it._checked = false }
    refreshItemCheckedMapFromSchedule()
}

fun clearAndPopulateWithStandardItems(){
    val today = LocalDate.now()

    _schedule.clear()

    insertScheduleEntry (FeedEntry(
        _type = EntryType.FEED,
        _complete = false,
        _repeats = true,
        _title = "Morning Feed",
        _time = LocalDateTime.of(today.year, today.month, today.dayOfMonth , 9,0),
        _amount = 50.0,
        _unit = EntryUnits.mL,
        _notes = "",
        _feedType = FeedType.BOLUS
    ))

    insertScheduleEntry (FeedEntry(
        _type = EntryType.FEED,
        _complete = false,
        _repeats = false,
        _title = "Mid-Day Feed",
        _time = LocalDateTime.of(today.year, today.month, today.dayOfMonth , 12,0),
        _amount = 50.0,
        _unit = EntryUnits.mL,
        _notes = "Afternoon Note",
        _feedType = FeedType.PUMP
    ))

    insertScheduleEntry( FeedEntry(
        _type = EntryType.FEED,
        _complete = false,
        _repeats = true,
        _title = "Evening Feed",
        _time = LocalDateTime.of(today.year, today.month, today.dayOfMonth , 18,0),
        _amount = 50.0,
        _unit = EntryUnits.mL,
        _notes = "",
        _feedType = FeedType.GRAVITY
    ))


    insertScheduleEntry( FlushEntry(
        _type = EntryType.FLUSH,
        _complete = false,
        _repeats = false,
        _title = "Afternoon Flush",
        _time = LocalDateTime.of(today.year, today.month, today.dayOfMonth , 13,0),
        _amount = 10.0,
        _unit = EntryUnits.mL,
        _notes = ""
    ))

    insertScheduleEntry( FlushEntry(
        _type = EntryType.FLUSH,
        _complete = false,
        _repeats = false,
        _title = "One-Time Evening Flush",
        _time = LocalDateTime.of(today.year, today.month, today.dayOfMonth , 19,0),
        _amount = 20.0,
        _unit = EntryUnits.mL,
        _notes = "Flush after feed"
    ))

    // --- Medication Entry (repeating) ---
    insertScheduleEntry( MedicationEntry(
        _type = EntryType.MEDICINE,
        _complete = false,
        _repeats = true,
        _title = "Mid-Day Medication",
        _time = LocalDateTime.of(today.year, today.month, today.dayOfMonth , 14,0),
        _amount = 5.0,
        _unit = EntryUnits.mg,
        _notes = "",
        _medType = MedType.ORAL,
        _medicationName = "Tylenol"
    ))
}


fun loadItemsFromFB(){

    _entryLog.clear()
    _schedule.clear()
    loadLogItemsFromFB()

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db = Firebase.firestore

    uid?.let {
        db.collection("users")
            .document(it)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if(documentSnapshot.exists()) {
                    val items = documentSnapshot.get("scheduleItems") as? List<Map<String, Any>>
                    if(items != null) {
                        val entryList = mutableListOf<Entry>()
                        for(itemMap in items) {
                            val entry = mapEntry(itemMap)
                            if(entry !=null) {
                                entryList.add(entry)
                            }
                        }

                        for (newItem in entryList){
                            insertScheduleEntry(newItem)
                        }
                    }
                }
            }
    }
}

private fun saveFullScheduleToFirestore() {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = FirebaseFirestore.getInstance()

    // Always write mapped objects; ensures _checked and time are serialized correctly.
    val mapped = _schedule.map { entryToFirestoreMap(it) }

    db.collection("users")
        .document(uid)
        .update("scheduleItems", mapped)
}

private fun dedupeSchedule() {
    if (_schedule.size <= 1) return

    val fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    fun sig(entry: Entry): String {
        val common = listOf(
            entry._type.name,
            entry._title.trim(),
            entry._time.format(fmt),
            entry._amount.toString(),
            entry._unit.name,
            entry._notes.trim()
        )
        val extra = when (entry) {
            is FeedEntry -> listOf("F", entry._feedType.name)
            is MedicationEntry -> listOf("M", entry._medType.name, entry._medicationName ?: "")
            is FlushEntry -> listOf("L")
            else -> emptyList()
        }
        return (common + extra).joinToString("|")
    }

    val keep = LinkedHashMap<String, Entry>()
    for (e in _schedule) {
        val k = sig(e)
        val existing = keep[k]
        if (existing == null) {
            keep[k] = e
        } else {
            // Prefer the one that's checked; otherwise keep the first.
            if (!existing._checked && e._checked) {
                keep[k] = e
            }
        }
    }
    if (keep.size != _schedule.size) {
        _schedule.clear()
        _schedule.addAll(keep.values)
        refreshItemCheckedMapFromSchedule()
    }
}