package com.example.aifarmingapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aifarmingapp.data.repository.Repository
import com.example.aifarmingapp.util.UiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val repository: Repository
): ViewModel() {

    private val _signInState = MutableLiveData<UiState<String>>()
    val signInState: LiveData<UiState<String>>
        get() = _signInState

    var isUserLoggedIn = false

    init {
        checkIfUserLoggedIn()
    }

    fun signIn(email: String, password: String) {
        _signInState.value = UiState.Loading
        repository.signIn(email, password) {
            _signInState.value = it
        }
    }

    private fun checkIfUserLoggedIn() {
        isUserLoggedIn = firebaseAuth.currentUser != null
    }
}
