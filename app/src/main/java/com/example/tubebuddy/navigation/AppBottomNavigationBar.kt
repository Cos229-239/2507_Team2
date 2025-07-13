package com.example.tubebuddy.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .clip(
                RoundedCornerShape(
                    32.dp,
                    32.dp
                )
            )
            .background(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(
                    topStart = 36.dp,
                    topEnd = 36.dp
                )
            )
    ) {
        bottomNavItems.forEach { item ->
            AddItem(
                screen = item,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavItem,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onBackground,
            selectedTextColor = MaterialTheme.colorScheme.onBackground,
            unselectedIconColor = MaterialTheme.colorScheme.onSurface,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
            indicatorColor = Color.Transparent
        ),
        label = { Text(text = screen.title) },
        icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true, //Check if the current route matches the item's route
        onClick = {
            // THE CORE OF THE NAVIGATION
            navController.navigate(screen.route) {
                // Pop up to the start destination of the graph to avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
        }
    )
}