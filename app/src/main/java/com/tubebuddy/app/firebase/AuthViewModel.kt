package com.tubebuddy.app.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    // Declare Auth
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState : StateFlow<AuthState> = _authState.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _authState.value = if(firebaseAuth.currentUser != null) {
                AuthState.Authenticated(firebaseAuth.currentUser!!)
            }
            else {
                AuthState.Unauthenticated
            }
        }
    }

    fun signIn(email : String, password : String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {

                        }
                        else {
                            val exception = task.exception
                            _authState.value = AuthState.Error(exception?.message ?: "An error occurred")
                        }
                    }
            }
            catch(e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun signUp(email : String, password : String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            val db = Firebase.firestore
                            user?.let {
                                val userData = User(
                                    email = email,
                                    createdAt = Timestamp.now(),
                                    onBoardComplete = false
                                )

                                db.collection("users").document(it.uid).set(userData)
                                    .addOnSuccessListener {

                                    }
                                    .addOnFailureListener {

                                    }
                            }
                        }
                        else {
                            val exception = task.exception
                            _authState.value = AuthState.Error(exception?.message ?: "An error occurred")
                        }
                    }
            }
            catch(e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }
}

