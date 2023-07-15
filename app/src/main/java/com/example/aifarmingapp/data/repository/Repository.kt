package com.example.aifarmingapp.data.repository

import com.example.aifarmingapp.util.UiState

interface Repository {

    fun signIn(email: String, pass: String, result: (UiState<String>) -> Unit)
    fun signUp(email: String, pass: String, result: (UiState<String>) -> Unit)
}