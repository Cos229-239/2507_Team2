package com.tubebuddy.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class InventoryItem(val name: String, val quantity: Int, val unit: String)

private val CardShape = RoundedCornerShape(16.dp)
private val PillShape = RoundedCornerShape(12.dp)

@Composable
fun InventoryScreen() {
    val items = remember {
        mutableStateListOf(
            InventoryItem("Feeding Tube Extension", 3, "pcs"),
            InventoryItem("G-Tube Pads", 12, "pcs"),
            InventoryItem("Pediasure Formula", 24, "cans"),
            InventoryItem("Flush Syringe", 20, "pcs")
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var itemPendingDelete by remember { mutableStateOf<InventoryItem?>(null) }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(bottom = 88.dp)
        ) {
            Text(
                text = "Inventory",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 8.dp)
            )

            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(items, key = { it.name + it.unit }) { item ->
                    InventoryItemCard(
                        item = item,
                        onDelete = { itemPendingDelete = item },
                        modifier = Modifier
                            .size(width = 380.dp, height = 94.dp)
                    )
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            onClick = { showAddDialog = true },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.tertiary
        ) {
            Text(text = "+", fontSize = 24.sp)
        }

        if (showAddDialog) {
            AddItemDialog(
                onDismiss = { showAddDialog = false },
                onSave = { name, qty, unit ->
                    items.add(InventoryItem(name.trim(), qty, unit.trim()))
                    showAddDialog = false
                }
            )
        }

        itemPendingDelete?.let { toDelete ->
            AlertDialog(
                onDismissRequest = { itemPendingDelete = null },
                title = { Text("Delete item?") },
                text = { Text("This will remove \"${toDelete.name}\" from your inventory.") },
                confirmButton = {
                    TextButton(onClick = {
                        items.remove(toDelete)
                        itemPendingDelete = null
                    }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = { TextButton(onClick = { itemPendingDelete = null }) { Text("Cancel") } }
            )
        }
    }
}

@Composable
private fun AddItemDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, quantity: Int, unit: String) -> Unit
) {
    val c = MaterialTheme.colorScheme
    val tfColors = TextFieldDefaults.colors(
        focusedContainerColor = c.onSurface,
        focusedTextColor = c.surface,
        focusedLabelColor = c.onSurface,
        unfocusedContainerColor = c.onSurface,
        unfocusedTextColor = c.surface,
        unfocusedLabelColor = c.surface,
        cursorColor = c.tertiary,
        focusedIndicatorColor = c.tertiary,
        unfocusedIndicatorColor = c.onSurface
    )

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(1) }
    var unit by remember { mutableStateOf("pcs") }
    var unitMenuExpanded by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }

    fun validateAndSave() {
        nameError = if (name.isBlank()) "Required" else null
        if (nameError == null) onSave(name.trim(), quantity, unit.trim())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.secondary, // matches Schedule sheets
        title = { Text("Add Inventory Item", color = MaterialTheme.colorScheme.onSecondary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item name") },
                    isError = nameError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = tfColors,
                    supportingText = { nameError?.let { Text(it, color = MaterialTheme.colorScheme.onSecondary) } }
                )

                Column {
                    Text(
                        "Quantity",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }, enabled = quantity > 1) {
                            Text("–", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondary)
                        }

                        OutlinedTextField(
                            value = quantity.toString(),
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true,
                            modifier = Modifier
                                .width(96.dp)
                                .padding(horizontal = 4.dp),
                            textStyle = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            colors = tfColors
                        )

                        IconButton(onClick = { if (quantity < 99) quantity++ }, enabled = quantity < 99) {
                            Icon(Icons.Filled.Add, contentDescription = "Increase", tint = MaterialTheme.colorScheme.onSecondary)
                        }
                    }

                    val row1 = listOf(1, 3, 5, 10)
                    val row2 = listOf(24, 48, 99)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                        row1.forEach { q ->
                            OutlinedButton(
                                onClick = { quantity = q },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSecondary),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary),
                                shape = PillShape,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.heightIn(min = 36.dp)
                            ) { Text(q.toString(), fontWeight = FontWeight.Medium) }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                        row2.forEach { q ->
                            OutlinedButton(
                                onClick = { quantity = q },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSecondary),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary),
                                shape = PillShape,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.heightIn(min = 36.dp)
                            ) { Text(q.toString(), fontWeight = FontWeight.Medium) }
                        }
                    }
                }

                val unitOptions = listOf("pcs", "cans", "bottles", "boxes", "ml", "oz")
                Box(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {},
                        label = { Text("Unit") },
                        readOnly = true,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { unitMenuExpanded = true },
                        trailingIcon = {
                            IconButton(onClick = { unitMenuExpanded = true }) {
                                Icon(Icons.Filled.ArrowDropDown, null, tint = MaterialTheme.colorScheme.onSecondary)
                            }
                        },
                        colors = tfColors
                    )
                    DropdownMenu(
                        expanded = unitMenuExpanded,
                        onDismissRequest = { unitMenuExpanded = false }
                    ) {
                        unitOptions.forEach { u ->
                            DropdownMenuItem(
                                text = { Text(u, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = { unit = u; unitMenuExpanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { validateAndSave() }) { Text("Save", color = MaterialTheme.colorScheme.onSecondary) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = MaterialTheme.colorScheme.onSecondary) } }
    )
}

@Composable
fun InventoryItemCard(
    item: InventoryItem,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLowStock = item.quantity <= 5

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = CardShape,
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
                    containerColor = if (isLowStock) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "${item.quantity} ${item.unit}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(start = 8.dp)
                )
                if (isLowStock) {
                    Text(
                        text = "Low stock",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.padding(end = 4.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete item")
            }
        }
    }
}
