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
    var _checked: Boolean
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
    val _feedType: FeedType,
    override var _checked: Boolean = false
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
    override var _checked: Boolean = false
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
    val _medicationName: String,
    override var _checked: Boolean = false
) : Entry

interface MedEntry{
    val _name: String
}

val _schedule = mutableStateListOf<Entry>()
val _entryLog = mutableStateListOf<Entry>()
val _medLog = mutableStateListOf<MedEntry>(
    object : MedEntry{
        override val _name: String
            get() = "Acetaminophen"
    },
    object : MedEntry{
        override val _name: String
            get() = "Amiodarone"
    },
    object : MedEntry{
        override val _name: String
            get() = "Amoxicillin"
    },
    object : MedEntry{
        override val _name: String
            get() = "Atorvastatin"
    },
    object : MedEntry{
        override val _name: String
            get() = "Azithromycin"
    },
    object : MedEntry{
        override val _name: String
            get() = "Carbamazepine"
    },
    object : MedEntry{
        override val _name: String
            get() = "Ciprofloxacin"
    },
    object : MedEntry{
        override val _name: String
            get() = "Clarithromycin"
    },
    object : MedEntry{
        override val _name: String
            get() = "Dexamethasone"
    },
    object : MedEntry{
        override val _name: String
            get() = "Digoxin"
    },
    object : MedEntry{
        override val _name: String
            get() = "Dolutegravir"
    },
    object : MedEntry{
        override val _name: String
            get() = "Furosemide"
    },
    object : MedEntry{
        override val _name: String
            get() = "Hydromorphone"
    },
    object : MedEntry{
        override val _name: String
            get() = "Ibuprofen"
    },
    object : MedEntry{
        override val _name: String
            get() = "Lansoprazole"
    },
    object : MedEntry{
        override val _name: String
            get() = "Levetiracetam"
    },
    object : MedEntry{
        override val _name: String
            get() = "Levofloxacin"
    },
    object : MedEntry{
        override val _name: String
            get() = "Levodopa"
    },
    object : MedEntry{
        override val _name: String
            get() = "Levothyroxine"
    },
    object : MedEntry{
        override val _name: String
            get() = "Metronidazole"
    },
    object : MedEntry{
        override val _name: String
            get() = "Morphine"
    },
    object : MedEntry{
        override val _name: String
            get() = "Omeprazole"
    },
    object : MedEntry{
        override val _name: String
            get() = "Oxycodone"
    },
    object : MedEntry{
        override val _name: String
            get() = "Pantoprazole"
    },
    object : MedEntry{
        override val _name: String
            get() = "Phenytoin"
    },
    object : MedEntry{
        override val _name: String
            get() = "Prednisone"
    },
    object : MedEntry{
        override val _name: String
            get() = "Psyllium"
    },
    object : MedEntry{
        override val _name: String
            get() = "Simvastatin"
    },
    object : MedEntry{
        override val _name: String
            get() = "Valproic Acid"
    },
    object : MedEntry{
        override val _name: String
            get() = "Warfarin"
    }
)
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