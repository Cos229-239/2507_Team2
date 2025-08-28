package com.tubebuddy.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

data class InventoryItem(val name: String, var quantity: Int, val unit: String)

private val CardShape = RoundedCornerShape(16.dp)
private val PillShape = RoundedCornerShape(12.dp)

fun mapInventoryItem(itemMap: Map<String, Any>): InventoryItem? {
    val inventoryItemData = InventoryItem(
        name = itemMap["name"] as? String ?: "",
        quantity = (itemMap["quantity"] as? Number)?.toInt() ?: 0,
        unit = itemMap["unit"] as? String ?: ""
    )
    return inventoryItemData
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen() {
    val items = remember { mutableStateListOf<InventoryItem>() }

    val db = Firebase.firestore
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var showAddDialog by remember { mutableStateOf(false) }
    var showAdjustInventory by remember { mutableStateOf(false) }
    var itemPendingDelete by remember { mutableStateOf<InventoryItem?>(null) }
    var itemBeingModified by remember { mutableStateOf("") }
    var itemsNeedUpdated by remember { mutableStateOf(true) }

    // Update LOCAL items list from db when needed
    if (itemsNeedUpdated) {
        items.clear()
        uid?.let {
            db.collection("users")
                .document(it)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val inventoryItems =
                            documentSnapshot.get("inventoryItems") as? List<Map<String, Any>>
                        if (inventoryItems != null) {
                            val itemList = mutableListOf<InventoryItem>()
                            for (itemMap in inventoryItems) {
                                val loadedItem = mapInventoryItem(itemMap)
                                if (loadedItem != null) {
                                    itemList.add(loadedItem)
                                }
                            }
                            items.addAll(itemList)
                        }
                    }
                    itemsNeedUpdated = false
                }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(bottom = 88.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Inventory:",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Start
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
                            .clickable(
                                onClick = {
                                    showAdjustInventory = true
                                    itemBeingModified = item.name
                                }
                            )
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
                    uid?.let {
                        db.collection("users").document(it)
                            .update("inventoryItems", FieldValue.arrayUnion(items.last()))
                            .addOnSuccessListener { document ->
                                showAddDialog = false
                                itemsNeedUpdated = true
                            }
                    }
                },
                items = items
            )
        }

        itemPendingDelete?.let { toDelete ->
            AlertDialog(
                onDismissRequest = { itemPendingDelete = null },
                title = { Text("Delete item?") },
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                containerColor = MaterialTheme.colorScheme.secondary,
                text = { Text("This will remove \"${toDelete.name}\" from your inventory.") },
                confirmButton = {
                    TextButton(onClick = {
                        // DELETE FROM DB
                        items.remove(toDelete)
                        uid?.let {
                            db.collection("users").document(it)
                                .update("inventoryItems", items)
                                .addOnSuccessListener { document ->
                                    itemPendingDelete = null
                                }
                        }
                    }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        itemPendingDelete = null
                    }) {
                        Text(
                            text = "Cancel",
                            color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            )
        }

        if (showAdjustInventory) {
            val item = items.find { it.name == itemBeingModified }
            var quantity by remember { mutableIntStateOf(item?.quantity ?: 0) }
            AlertDialog(
                onDismissRequest = {
                    showAdjustInventory = false
                    itemBeingModified = ""
                },
                title = {
                    Text(
                        text = "Modify ${item?.name} Amount",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                containerColor = MaterialTheme.colorScheme.secondary,
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 16.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                enabled = quantity > 1
                            ) {
                                Text(
                                    "–",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
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
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                                    focusedTextColor = MaterialTheme.colorScheme.surface,
                                    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.surface,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.surface
                                )
                            )

                            IconButton(
                                onClick = { if (quantity < 99) quantity++ },
                                enabled = quantity < 99
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Increase",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Modify DB inventory quantity
                            if (item?.quantity != quantity) {
                                item?.quantity = quantity
                                uid?.let {
                                    db.collection("users").document(it)
                                        .update("inventoryItems", items)
                                        .addOnSuccessListener { document ->
                                            itemBeingModified = ""
                                            showAdjustInventory = false
                                            itemsNeedUpdated = true
                                        }
                                }
                            } else {
                                showAdjustInventory = false
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    }
}

@Composable
private fun AddItemDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, quantity: Int, unit: String) -> Unit,
    items: List<InventoryItem>
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(1) }
    var unit by remember { mutableStateOf("pcs") }
    var unitMenuExpanded by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }

    fun validateAndSave() {
        nameError =
            if(name.isBlank()) {
                "Required"
            }
            else if(items.any { it.name == name }) {
                "Item already exists"
            }
        else null
        if (nameError == null) onSave(name.trim(), quantity, unit.trim())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.secondary, // matches Schedule sheets
        title = { Text("Add Inventory Item") },
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item name") },
                    isError = nameError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                        focusedTextColor = MaterialTheme.colorScheme.surface,
                        focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.surface,
                        unfocusedLabelColor = MaterialTheme.colorScheme.surface),
                    supportingText = { nameError?.let { Text(it, color = MaterialTheme.colorScheme.onSecondary) } }
                )

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Quantity",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 16.dp)
                    ) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }, enabled = quantity > 1) {
                            Text("–", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
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
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                                focusedTextColor = MaterialTheme.colorScheme.surface,
                                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.surface,
                                unfocusedLabelColor = MaterialTheme.colorScheme.surface)
                        )

                        IconButton(onClick = { if (quantity < 99) quantity++ }, enabled = quantity < 99) {
                            Icon(Icons.Filled.Add, contentDescription = "Increase", tint = MaterialTheme.colorScheme.onBackground)
                        }
                    }

                    val row1 = listOf(1, 3, 5, 10)
                    val row2 = listOf(24, 48, 96, 100)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        row1.forEach { q ->
                            OutlinedButton(
                                onClick = { quantity = q },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                                shape = PillShape,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.heightIn(min = 36.dp)
                            ) { Text(q.toString(), fontWeight = FontWeight.Medium) }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row2.forEach { q ->
                            OutlinedButton(
                                onClick = { quantity = q },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
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
                                Icon(Icons.Filled.ArrowDropDown, null, tint = MaterialTheme.colorScheme.onBackground)
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                            focusedTextColor = MaterialTheme.colorScheme.surface,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.surface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.surface)
                    )
                    DropdownMenu(
                        expanded = unitMenuExpanded,
                        onDismissRequest = { unitMenuExpanded = false }
                    ) {
                        unitOptions.forEach { u ->
                            DropdownMenuItem(
                                text = { Text(u, color = MaterialTheme.colorScheme.onBackground) },
                                onClick = { unit = u; unitMenuExpanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { validateAndSave() },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = MaterialTheme.colorScheme.onBackground) } }
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
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                border = if(isLowStock) BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    else BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
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