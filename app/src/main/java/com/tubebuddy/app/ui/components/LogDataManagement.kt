package com.tubebuddy.app.ui.components

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.tubebuddy.app.screens.insertEntry
import com.tubebuddy.app.screens.mapEntry
import java.time.LocalDateTime

//serializer helper
fun LocalDateTime.toFirestoreTimeMap(): Map<String, Int> = mapOf(
    "year" to year,
    "monthValue" to monthValue,
    "dayOfMonth" to dayOfMonth,
    "hour" to hour,
    "minute" to minute,
    "second" to second
)

//serializer
fun entryToFirestoreMap(currEntry: Entry): Map<String, Any?> {
    val base = mutableMapOf<String, Any?>(
        "_kind" to when (currEntry) {
            is FeedEntry -> "FEED"
            is FlushEntry -> "FLUSH"
            is MedicationEntry -> "MEDICINE"
            else -> currEntry._type.toString()
        },
        "_type" to currEntry._type.toString(),
        "_complete" to currEntry._complete,
        "_repeats" to currEntry._repeats,
        "_title" to currEntry._title,
        "_time" to currEntry._time.toFirestoreTimeMap(),
        "_amount" to currEntry._amount,
        "_unit" to currEntry._unit.toString(),
        "_notes" to currEntry._notes,
        "_checked" to currEntry._checked
    )

    when (currEntry) {
        is FeedEntry -> {
            base["_feedType"] = currEntry._feedType.toString()
        }
        is MedicationEntry -> {
            base["_medType"] = currEntry._medType.toString()
            base["_medicationName"] = currEntry._medicationName
        }
    }

    return base
}

//uploads schedule item one at a time to FB
fun pushScheduleItemFB(schedItem: Entry) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = Firebase.firestore
    val itemMap = entryToFirestoreMap(schedItem)

    db.collection("users")
        .document(uid)
        .update("scheduleItems", FieldValue.arrayUnion(itemMap))
        .addOnFailureListener {
            db.collection("users")
                .document(uid)
                .set(mapOf("scheduleItems" to listOf(itemMap)), com.google.firebase.firestore.SetOptions.merge())
        }
}

//uploads log item one at a time to FB
fun pushLogItemToFirestore(schedItem: Entry) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = Firebase.firestore
    val itemMap = entryToFirestoreMap(schedItem)

    db.collection("users")
        .document(uid)
        .update("logItems", FieldValue.arrayUnion(itemMap))
        .addOnFailureListener {
            db.collection("users")
                .document(uid)
                .set(mapOf("logItems" to listOf(itemMap)), com.google.firebase.firestore.SetOptions.merge())
        }
}

//Loads all log items from FB
fun loadLogItemsFromFB(){
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db = Firebase.firestore

    uid?.let {
        db.collection("users")
            .document(it)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if(documentSnapshot.exists()) {
                    val items = documentSnapshot.get("logItems") as? List<Map<String, Any>>
                    if(items != null) {
                        val entryList = mutableListOf<Entry>()
                        for(itemMap in items) {
                            val entry = mapEntry(itemMap)
                            if(entry !=null) {
                                entryList.add(entry)
                            }
                        }

                        for (newItem in entryList){
                            insertEntry(newItem)
                        }
                    }
                }
            }
    }
}

//Deletes a single schedule entry from FB
fun deleteScheduleItemFB(item: Entry) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = Firebase.firestore
    val itemMap = entryToFirestoreMap(item)

    db.collection("users")
        .document(uid)
        .update("scheduleItems", FieldValue.arrayRemove(itemMap))
}

//Deletes a single log entry from FB
fun deleteLogItemFB(item: Entry) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = Firebase.firestore
    val itemMap = entryToFirestoreMap(item)

    db.collection("users")
        .document(uid)
        .update("logItems", FieldValue.arrayRemove(itemMap))
}

//Refreshes state of check boxes for to-do list style schedule interface
fun refreshItemCheckedMapFromSchedule() {
    itemCheckedMap.clear()
    _schedule.forEach { entry ->
        itemCheckedMap[entry] = entry._checked
    }
}

fun setChecked(entry: Entry, checked: Boolean) {
    entry._checked = checked
    itemCheckedMap[entry] = checked
}