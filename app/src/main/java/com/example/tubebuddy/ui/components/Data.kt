package com.example.tubebuddy.ui.components
import androidx.compose.runtime.mutableStateListOf
import java.time.LocalDateTime

enum class EntryType{
    FEED, FLUSH, MEDICINE
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
    override val _notes: String
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