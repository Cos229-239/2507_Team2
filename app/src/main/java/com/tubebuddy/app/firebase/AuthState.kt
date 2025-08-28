package com.tubebuddy.app.firebase

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

data class User(
    val name: String = "",
    val email: String = "",
    val createdAt: Timestamp? = null,
    val onBoardComplete: Boolean = false,
)