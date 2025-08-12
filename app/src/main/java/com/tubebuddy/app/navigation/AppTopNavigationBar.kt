package com.tubebuddy.app.navigation

import android.R
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tubebuddy.app.ui.components.FilterType
import com.tubebuddy.app.ui.components._currFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopNavBar(navController : NavHostController) {
    // Get current back stack entry // Set as current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var isFilterMenuTapped by remember { mutableStateOf(false) }

    // Determine screen
    val currentScreen = when(currentRoute) {
        Screen.Home.route -> Screen.Home
        Screen.FeedingLog.route -> Screen.FeedingLog
        Screen.Schedule.route -> Screen.Schedule
        Screen.Medication.route -> Screen.Medication
        Screen.SiteCare.route -> Screen.SiteCare
        Screen.Inventory.route -> Screen.Inventory
        Screen.Profile.route -> Screen.Profile
        // Future screens get added here
        else -> null // Could set up a default unknown screen here
    }
    CenterAlignedTopAppBar(
        title = {
            Text(currentScreen?.title?: "Welcome, [Username]")
        },
        navigationIcon = {
            // Shows back button if not at start destination


//            val isStartDestination = currentRoute == Screen.Home.route
//            if(!isStartDestination && navController.previousBackStackEntry != null) {
//                IconButton(onClick = { navController.navigateUp() }) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                        contentDescription = "Back"
//                    )
//                }
//            }


            // add burger here later
            // else if current == home.route = showDrawer
        },
        actions = {
            if (currentScreen == Screen.Schedule || currentScreen == Screen.FeedingLog) {
                IconButton(onClick = {
                    isFilterMenuTapped = true
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Filter",
                        modifier = Modifier.size(28.dp)
                    )
                }

                DropdownMenu(
                    expanded = isFilterMenuTapped,
                    onDismissRequest = { isFilterMenuTapped = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Show All", color = MaterialTheme.colorScheme.surface) },
                        onClick = {
                            isFilterMenuTapped = false
                            _currFilter.value = FilterType.ALL_FILTER
                        },
                        trailingIcon = {
                            if (_currFilter.value == FilterType.ALL_FILTER) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "All Items Filter"
                                )
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Feeds", color = MaterialTheme.colorScheme.surface) },
                        onClick = {
                            isFilterMenuTapped = false
                            _currFilter.value = FilterType.FEED_FILTER
                        },
                        trailingIcon = {
                            if (_currFilter.value == FilterType.FEED_FILTER) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Feed Items Filter"
                                )
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Flushes", color = MaterialTheme.colorScheme.surface) },
                        onClick = {
                            isFilterMenuTapped = false
                            _currFilter.value = FilterType.FLUSH_FILTER
                        },
                        trailingIcon = {
                            if (_currFilter.value == FilterType.FLUSH_FILTER) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Flush Items Filter"
                                )
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Medications", color = MaterialTheme.colorScheme.surface) },
                        onClick = {
                            isFilterMenuTapped = false
                            _currFilter.value = FilterType.MEDICINE_FILTER
                        },
                        trailingIcon = {
                            if (_currFilter.value == FilterType.MEDICINE_FILTER) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Medication Items Filter"
                                )
                            }
                        }
                    )
                }
            }

            IconButton(onClick = {
                navController.navigate(Screen.Profile.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                }
            } ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    Modifier.size(36.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        modifier = Modifier.clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
    )
}