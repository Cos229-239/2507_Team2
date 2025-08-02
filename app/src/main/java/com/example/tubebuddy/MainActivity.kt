package com.example.tubebuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.tubebuddy.navigation.AppBottomNavBar
import com.example.tubebuddy.navigation.AppNavHost
import com.example.tubebuddy.navigation.AppTopNavBar
import com.example.tubebuddy.ui.theme.TubeBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TubeBuddyTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    MainScreenContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent() {
    // Remember navController
    val navController = rememberNavController()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopNavBar(navController = navController)
        },
        bottomBar = {
            AppBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}


// *** PREVIEW *** //
@Preview(showBackground = false)
@Composable
fun GreetingPreview() {
    TubeBuddyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            MainScreenContent()
        }
    }
}