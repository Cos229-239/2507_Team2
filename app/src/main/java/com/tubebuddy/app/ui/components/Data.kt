package com.tubebuddy.app.ui.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.time.LocalDateTime

val itemCheckedMap = mutableStateMapOf<Entry, Boolean>()

enum class EntryType{
    FEED, FLUSH, MEDICINE
}

enum class FilterType {
    ALL_FILTER, FEED_FILTER, FLUSH_FILTER, MEDICINE_FILTER
}

enum class EntryUnits{
    mL, mg, g
}

enum class FeedType{
    BOLUS, GRAVITY, PUMP, ORAL
}

enum class MedType{
    ORAL, IV
}

interface Entry{
    val _type: EntryType
    val _complete: Boolean
    val _repeats: Boolean
    val _title: String
    val _time: LocalDateTime
    val _amount: Double
    val _unit: EntryUnits
    val _notes: String
}

data class FeedEntry(
    override val _type: EntryType,
    override val _complete: Boolean,
    override val _repeats: Boolean,
    override val _title: String,
    override val _time: LocalDateTime,
    override val _amount: Double,
    override val _unit: EntryUnits,
    override val _notes: String,
    val _feedType: FeedType
) : Entry

data class FlushEntry(
    override val _type: EntryType,
    override val _complete: Boolean,
    override val _repeats: Boolean,
    override val _title: String,
    override val _time: LocalDateTime,
    override val _amount: Double,
    override val _unit: EntryUnits,
    override val _notes: String,
) : Entry

data class MedicationEntry(
    override val _type: EntryType,
    override val _complete: Boolean,
    override val _repeats: Boolean,
    override val _title: String,
    override val _time: LocalDateTime,
    override val _amount: Double,
    override val _unit: EntryUnits,
    override val _notes: String,
    val _medType: MedType,
    val _medicationName: String
) : Entry

val _schedule = mutableStateListOf<Entry>()
val _entryLog = mutableStateListOf<Entry>()
var _currFilter = mutableStateOf(FilterType.ALL_FILTER)


//// Site Care Data ////
data class TaskItemData(
    val id: Long, // unique id
    val text: String,
    val isChecked: Boolean = false // state
)

data class CompletedTaskItemData(
    val id: Long,
    val dateTime: String,
    val title: String,
    val description: String
)

val tasks =
    mutableStateListOf(
        TaskItemData(1, "Clean Tube Site", false),
        TaskItemData(2, "Redness/Granulation Check", false),
        TaskItemData(3, "Apply Barrier Cream", false),
        TaskItemData(4, "Change Dressing", false)
    )

val completedTasks = mutableStateListOf<CompletedTaskItemData>()
//// END Site Care Data ////