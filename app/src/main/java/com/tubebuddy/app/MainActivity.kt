package com.tubebuddy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import com.tubebuddy.app.firebase.AuthState
import com.tubebuddy.app.firebase.AuthViewModel
import com.tubebuddy.app.navigation.AppBottomNavBar
import com.tubebuddy.app.navigation.AppNavHost
import com.tubebuddy.app.navigation.AppTopNavBar
import com.tubebuddy.app.screens.AuthForm
import com.tubebuddy.app.screens.OnboardingContent
import com.tubebuddy.app.ui.theme.ThemeStateHolder
import com.tubebuddy.app.ui.theme.TubeBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by ThemeStateHolder.isDarkTheme
            TubeBuddyTheme(isDarkTheme) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    AuthScreen()
                }
            }
        }
    }
}

@Composable
fun AuthScreen(authViewModel: AuthViewModel = viewModel()) {
    val authState by authViewModel.authState.collectAsState()
    when(authState) {
        is AuthState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is AuthState.Authenticated -> {
            val user = (authState as AuthState.Authenticated).user
            val db = Firebase.firestore
            val onBoardCompleteState = remember { mutableStateOf<Boolean?>(null) }
            LaunchedEffect(user.uid) {
                db.collection("users").document(user.uid).get()
                    .addOnSuccessListener { document ->
                        if(document.exists()) {
                            onBoardCompleteState.value = document.getBoolean("onBoardComplete")
                        }
                        else {
                            onBoardCompleteState.value = false
                        }
                    }
                    .addOnFailureListener { onBoardCompleteState.value = false }
            }
            when(onBoardCompleteState.value) {
                null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                true -> {
                    MainScreenContent(user, onSignOut = { authViewModel.signOut() })
                }
                false -> {
                    OnboardingContent(user, onFinished = { onBoardCompleteState.value = true })
                }
            }
        }
        is AuthState.Unauthenticated, is AuthState.Error -> {
            AuthForm(
                onSignIn = { email, password -> authViewModel.signIn(email, password) },
                onSignUp = { email, password -> authViewModel.signUp(email, password) },
                errorMessage = if(authState is AuthState.Error) (authState as AuthState.Error).message else null
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(user: FirebaseUser, onSignOut: () -> Unit) {
    // I believe the user and maybe not the signout function can be passed to nav controller for more optimized calls to db
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