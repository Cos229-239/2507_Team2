package com.example.tubebuddy.navigation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopNavBar(navController : NavHostController) {
    // Get current back stack entry // Set as current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine screen
    val currentScreen = when(currentRoute) {
        Screen.Home.route -> Screen.Home
        Screen.FeedingLog.route -> Screen.FeedingLog
        Screen.Schedule.route -> Screen.Schedule
        Screen.Medication.route -> Screen.Medication
        Screen.SiteCare.route -> Screen.SiteCare
        // Future screens get added here
        else -> null // Could set up a default unknown screen here
    }

    CenterAlignedTopAppBar(
        title = {
            Text(currentScreen?.title?: "Welcome, [Username]")
        },
        navigationIcon = {
            // Shows back button if not at start destination
            val isStartDestination = currentRoute == Screen.Home.route
            if(!isStartDestination && navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
            // add burger here later
            // else if current == home.route = showDrawer
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
    )
}