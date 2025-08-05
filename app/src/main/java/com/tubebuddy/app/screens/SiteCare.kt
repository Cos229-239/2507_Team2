package com.tubebuddy.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tubebuddy.app.ui.components.BuddyCard
import com.tubebuddy.app.ui.components.CompletedTaskItemData
import com.tubebuddy.app.ui.components.TaskItemData
import com.tubebuddy.app.ui.components.completedTasks
import com.tubebuddy.app.ui.components.tasks
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CreateTaskItem(
    task: TaskItemData,
    onCheckedChange: (TaskItemData, Boolean) -> Unit
) {
    Row(
        modifier = Modifier.padding(start = 8.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isChecked,
            onCheckedChange = { newCheckedState ->
                onCheckedChange(task, newCheckedState)
            },
            colors = CheckboxColors(
                uncheckedBorderColor = MaterialTheme.colorScheme.onBackground,
                uncheckedBoxColor = MaterialTheme.colorScheme.secondary,
                uncheckedCheckmarkColor = MaterialTheme.colorScheme.background,
                checkedCheckmarkColor = MaterialTheme.colorScheme.tertiary,
                checkedBoxColor = MaterialTheme.colorScheme.surface,
                checkedBorderColor = MaterialTheme.colorScheme.surface,
                disabledBorderColor = MaterialTheme.colorScheme.background,
                disabledUncheckedBorderColor = MaterialTheme.colorScheme.background,
                disabledIndeterminateBorderColor = MaterialTheme.colorScheme.background,
                disabledCheckedBoxColor = MaterialTheme.colorScheme.background,
                disabledUncheckedBoxColor = MaterialTheme.colorScheme.background,
                disabledIndeterminateBoxColor = MaterialTheme.colorScheme.background,
            )
        )
        Text(
            text = task.text,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteCareScreen() {
    val allTasksComplete by remember {
        derivedStateOf {
            tasks.all { it.isChecked }
        }
    }

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(allTasksComplete) {
        if (allTasksComplete) {
            showCompleteDialog = true
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        modifier = Modifier
            .fillMaxSize(),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Today's Tasks:",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.5f)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Row {
                    Column {
                        LazyColumn(
                            modifier = Modifier
                                .width(320.dp)
                                .padding(start = 8.dp, end = 8.dp),
                            horizontalAlignment = Alignment.Start,
                            userScrollEnabled = true
                        ) {
                            items(
                                items = tasks,
                                key = { task -> task.id }
                            ) { task ->
                                CreateTaskItem(
                                    task = task,
                                    onCheckedChange = { changedTask, newCheckedState ->
                                        val index = tasks.indexOfFirst { it.id == changedTask.id }
                                        if (index != -1) {
                                            tasks[index] =
                                                tasks[index].copy(isChecked = newCheckedState)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    val scrollState = rememberScrollState()
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, end = 8.dp)
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.End
                    ) {
                        SmallFloatingActionButton (
                            onClick = { showAddTaskDialog = true },
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.tertiary,
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 12.dp)
                        ) {
                            Icon(Icons.Filled.Add, "Add new task")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = "Recently Completed:",
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(completedTasks) {
                        BuddyCard(it.dateTime, it.title, it.description)
                    }
                }
            }
        }
    }

    if(showAddTaskDialog) {
        var newTaskText by remember { mutableStateOf("")}
        AlertDialog(
            onDismissRequest = {
                showAddTaskDialog = false
                newTaskText = ""
            },
            title = { Text("Add New Task")},
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.secondary,
            text = {
                OutlinedTextField(
                    value = newTaskText,
                    onValueChange = { newTaskText = it },
                    label = { Text("Task Name")},
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.surface
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                        focusedTextColor = MaterialTheme.colorScheme.surface,
                        focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.surface,
                        unfocusedLabelColor = MaterialTheme.colorScheme.surface)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if(newTaskText.isNotBlank()) {
                            val newId = System.currentTimeMillis()
                            tasks.add(TaskItemData(id = newId, text = newTaskText.trim()))
                            newTaskText = ""
                            showAddTaskDialog = false
                        }
                    },
                    enabled = newTaskText.isNotBlank(),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        disabledContentColor = MaterialTheme.colorScheme.secondary,
                        disabledContainerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        newTaskText = ""
                        showAddTaskDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if(showCompleteDialog) {
        var newNote by remember { mutableStateOf("")}
        AlertDialog(
            onDismissRequest = {
                showCompleteDialog = false
                newNote = ""
            },
            title = { Text("Daily Tasks Completed!") },
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.secondary,
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Would you like to add any notes?",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
                    OutlinedTextField(
                        value = newNote,
                        onValueChange = { newNote = it },
                        label = { Text("Notes") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.surface
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                            focusedTextColor = MaterialTheme.colorScheme.surface,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.surface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.surface)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newId = System.currentTimeMillis()
                        val currentDate = LocalDate.now()
                        val formatter = DateTimeFormatter.ofPattern("MM-dd")
                        val formattedDate = currentDate.format(formatter)
                        var numberOfTasksComplete = 0
                        tasks.forEach { element ->
                            if(element.isChecked) {
                                numberOfTasksComplete++
                            }
                        }

                        tasks.replaceAll { it.copy(isChecked = false)}
                        completedTasks.add(0, CompletedTaskItemData(
                            id = newId,
                            dateTime = formattedDate.toString(),
                            title = "$numberOfTasksComplete out of ${tasks.size} tasks complete",
                            description = "Notes: $newNote"
                        )
                        )
                        showCompleteDialog = false
                        newNote = ""
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Log Daily Tasks")
                }
            }
        )
    }
}