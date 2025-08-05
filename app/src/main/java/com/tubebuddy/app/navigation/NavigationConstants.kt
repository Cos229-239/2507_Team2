package com.tubebuddy.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home_screen", "Welcome, [Username]")
    object Schedule : Screen("schedule_screen", "Schedule")
    object FeedingLog : Screen("feedingLog_screen", "Feeding Log")
    object Medication : Screen("medication_screen", "Medications")
    object SiteCare : Screen("siteCare_screen", "Daily Site Care")
    object Inventory : Screen("inventory_Screen", "Inventory")
    object Profile : Screen("profile_Screen", "Profile")
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Home",
        icon = Icons.Rounded.Home,
        route = Screen.Home.route
    ),
    BottomNavItem(
        title = "Schedule",
        icon = Icons.Rounded.Menu,
        route = Screen.Schedule.route
    ),
    BottomNavItem(
        title = "Log",
        icon = Icons.Rounded.DateRange,
        route = Screen.FeedingLog.route
    ),
    BottomNavItem(
        title = "Inventory",
        icon = Icons.Rounded.Info,
        route = Screen.Inventory.route
    ),
    BottomNavItem(
        title = "Site Care",
        icon = Icons.Rounded.Favorite,
        route = Screen.SiteCare.route
    ),
    // KEEPING THIS HERE UNTIL FULLY IMPLEMENTED
//    BottomNavItem(
//        title = "Medication",
//        icon = Icons.Rounded.Face,
//        route = Screen.Medication.route
//    )
)