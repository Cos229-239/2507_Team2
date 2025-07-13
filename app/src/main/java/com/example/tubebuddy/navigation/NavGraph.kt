package com.example.tubebuddy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tubebuddy.screens.FeedingLogScreen
import com.example.tubebuddy.screens.HomeScreen
import com.example.tubebuddy.screens.MedicationScreen
import com.example.tubebuddy.screens.ScheduleScreen
import com.example.tubebuddy.screens.SiteCareScreen


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
            composable(Screen.Medication.route) {
                MedicationScreen()
            }
            composable(Screen.SiteCare.route) {
                SiteCareScreen()
            }
    }
}