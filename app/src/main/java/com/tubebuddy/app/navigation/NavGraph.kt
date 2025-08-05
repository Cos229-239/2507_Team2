package com.tubebuddy.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tubebuddy.app.screens.FeedingLogScreen
import com.tubebuddy.app.screens.HomeScreen
import com.tubebuddy.app.screens.MedicationScreen
import com.tubebuddy.app.screens.ScheduleScreen
import com.tubebuddy.app.screens.SiteCareScreen
import com.tubebuddy.app.screens.InventoryScreen
import com.tubebuddy.app.screens.ProfileScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route, // default screen
            modifier = modifier
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Schedule.route) {
                ScheduleScreen()
            }
            composable(Screen.FeedingLog.route) {
                FeedingLogScreen()
            }
            composable(Screen.Medication.route) { //NEEDS TO BE REMOVED
                MedicationScreen()
            }
            composable(Screen.SiteCare.route) {
                SiteCareScreen()
            }
            composable(Screen.Inventory.route) {
                InventoryScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
    }
}