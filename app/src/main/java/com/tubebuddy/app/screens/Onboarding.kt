package com.tubebuddy.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore


@Composable
fun OnboardingContent(user: FirebaseUser, onFinished: () -> Unit) {
    val onboardingStep = remember { mutableIntStateOf(0) }
    var username by remember { mutableStateOf("")}
    val db = Firebase.firestore
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp, start = 32.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            if(onboardingStep.intValue != 0) {
                Button(
                    onClick = {
                        if(onboardingStep.intValue < 3) {
                            onboardingStep.intValue--
                        }},
                    modifier = Modifier.fillMaxWidth(.22f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text(
                        text = "Back",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f))
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxHeight(.75f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when(onboardingStep.intValue) {
                0 -> {
                    Text(
                        text = "Welcome to TubeBuddy!",
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Your Tube Feeding Companion",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                1 -> {
                    Text(
                        text = "What TubeBuddy\nhas to offer:",
                        textAlign = TextAlign.Center,
                        style = TextStyle(lineHeight = 36.sp),
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "-Create a custom schedule\n" +
                                "-Auto populate logs\n" +
                                "-Track your inventory\n" +
                                "-Daily Site Care checklist\n" +
                                "-and more!",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                }
                2 -> {
                    Text(
                        text = "Let's set up some\ninformation about you!",
                        textAlign = TextAlign.Center,
                        style = TextStyle(lineHeight = 36.sp),
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.surface
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                            focusedTextColor = MaterialTheme.colorScheme.surface,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.surface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp, end = 32.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                onClick = {
                    if(onboardingStep.intValue < 2) {
                        onboardingStep.intValue++
                    }
                    else {
                        if(username.isNotEmpty()) {
                            user.uid.let {
                                db.collection("users")
                                    .document(it)
                                    .update("name", username, "onBoardComplete", true)
                                    .addOnSuccessListener {
                                       onFinished()
                                    }
                            }
                        }
                        else {
                            // add error message here?
                        }
                    }},
                modifier = Modifier.fillMaxWidth(.22f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.tertiary)) {
                Text(
                    if(onboardingStep.intValue < 2) {
                        "Next"
                    }
                    else {
                        "Go!"
                    }
                )
            }
        }
    }
}