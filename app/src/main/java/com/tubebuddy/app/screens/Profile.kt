package com.tubebuddy.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.tubebuddy.app.firebase.AuthViewModel
import com.tubebuddy.app.ui.theme.ThemeStateHolder

@Composable
fun ProfileScreen(authViewModel: AuthViewModel) {
    val db = Firebase.firestore
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val usernameState = remember { mutableStateOf<String?>(null) }
    val isDarkTheme by ThemeStateHolder.isDarkTheme
    uid?.let {
        db.collection("users").document(it).get()
            .addOnSuccessListener { document ->
                if(document.exists()) {
                    usernameState.value = document.getString("name")
                }
            }
    }
    if(usernameState.value == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(usernameState.value == null) {
                    CircularProgressIndicator()
                }
                else {
                    Card(
                        modifier = Modifier
                            .fillMaxHeight(.7f)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Text(
                            text = "${usernameState.value}'s Settings",
                            fontSize = 24.sp,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onBackground)

                        // BEGIN DARK MODE
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Toggle Dark Mode",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 16.dp),
                                color = MaterialTheme.colorScheme.onBackground)
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { isChecked ->
                                    ThemeStateHolder.isDarkTheme.value = isChecked
                                },
                                modifier = Modifier.padding(end = 16.dp),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.tertiary,
                                    checkedTrackColor = MaterialTheme.colorScheme.surface,
                                    checkedBorderColor = MaterialTheme.colorScheme.tertiary,
                                    checkedIconColor = MaterialTheme.colorScheme.tertiary,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                                    uncheckedTrackColor = MaterialTheme.colorScheme. onSurface,
                                    uncheckedBorderColor = MaterialTheme.colorScheme.surface,
                                    uncheckedIconColor = MaterialTheme.colorScheme.surface,
                                )
                            )
                        }
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp,  bottom = 16.dp)
                        )

                        // BEGIN


                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Button(
                                onClick = { },
                                modifier = Modifier.padding(top = 32.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Text("Change User Info")
                            }

                            Button(
                                onClick = { authViewModel.signOut() },
                                modifier = Modifier.padding(top = 32.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Text("Sign Out")
                            }
                        }
                    }
                }
            }
        }
    }
}