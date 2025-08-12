package com.tubebuddy.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tubebuddy.app.firebase.AuthViewModel
import com.tubebuddy.app.screens.FeedingLogScreen
import com.tubebuddy.app.screens.HomeScreen
import com.tubebuddy.app.screens.InventoryScreen
import com.tubebuddy.app.screens.ProfileScreen
import com.tubebuddy.app.screens.ScheduleScreen
import com.tubebuddy.app.screens.SiteCareScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
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
            composable(Screen.SiteCare.route) {
                SiteCareScreen()
            }
            composable(Screen.Inventory.route) {
                InventoryScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(authViewModel)
            }
    }
}